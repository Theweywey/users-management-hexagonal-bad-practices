package com.jcaa.usersmanagement.infrastructure.adapter.email;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import com.jcaa.usersmanagement.domain.exception.EmailSenderException;
import com.jcaa.usersmanagement.domain.model.EmailDestinationModel;
import java.lang.reflect.Field;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * Pruebas unitarias para JavaMailEmailSenderAdapter.
 * <p>Verifica la integración con la API de JavaMail, cubriendo:
 * <ul>
 * <li>Despacho exitoso de mensajes (Happy Path) usando MockedStatic.</li>
 * <li>Manejo de excepciones y envoltura en EmailSenderException.</li>
 * <li>Configuración correcta del Autenticador SMTP mediante reflexión.</li>
 * </ul>
 * Clean Code - Regla 11: Documentación y estructura AAA.
 */
@DisplayName("Pruebas Unitarias: JavaMailEmailSenderAdapter")
class JavaMailEmailSenderAdapterTest {

  private static final String HOST = "smtp.example.com";
  private static final int PORT = 587;
  private static final String USERNAME = "user@example.com";
  private static final String PASSWORD = "secret";
  private static final String FROM_ADDRESS = "noreply@example.com";
  private static final String FROM_NAME = "App Notifications";
  private static final String DEST_EMAIL = "john@example.com";
  private static final String DEST_NAME = "John Doe";
  private static final String SUBJECT = "Account created";
  private static final String BODY = "<html>Welcome</html>";

  private JavaMailEmailSenderAdapter adapter;
  private EmailDestinationModel destination;

  @BeforeEach
  void setUp() {
    final SmtpConfig config =
            new SmtpConfig(HOST, PORT, USERNAME, PASSWORD, FROM_ADDRESS, FROM_NAME);
    adapter = new JavaMailEmailSenderAdapter(config);
    destination = new EmailDestinationModel(DEST_EMAIL, DEST_NAME, SUBJECT, BODY);
  }

  @Test
  @DisplayName("Debe invocar Transport.send() exactamente una vez cuando el envío SMTP es exitoso")
  void shouldDispatchMessageWhenSmtpSucceeds() {
    // Arrange
    try (final MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {

      // Act
      adapter.send(destination);

      // Assert
      mockedTransport.verify(() -> Transport.send(any(Message.class)));
    }
  }

  @Test
  @DisplayName("Debe lanzar EmailSenderException cuando el transporte SMTP falla")
  void shouldThrowEmailSenderExceptionWhenTransportFails() {
    // Arrange
    final MessagingException smtpError = new MessagingException("Connection refused");
    try (final MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
      mockedTransport.when(() -> Transport.send(any(Message.class))).thenThrow(smtpError);

      // Act & Assert
      final EmailSenderException exception =
              assertThrows(EmailSenderException.class, () -> adapter.send(destination));

      assertAll("Validación de excepción de envío",
              () -> assertNotNull(exception.getMessage(), "El mensaje de la excepción no debe ser nulo"),
              () -> assertTrue(exception.getMessage().contains(DEST_EMAIL),
                      "El mensaje de error debe identificar al destinatario: " + DEST_EMAIL)
      );
    }
  }

  @Test
  @SuppressWarnings("java:S3011") // Reflexión necesaria para acceder al estado interno de la Session
  @DisplayName("Debe proveer las credenciales configuradas en SmtpConfig al Autenticador")
  void shouldProvideConfiguredCredentialsWhenAuthenticatorIsInvoked() throws Exception {
    // Arrange
    final Field sessionField = JavaMailEmailSenderAdapter.class.getDeclaredField("mailSession");
    sessionField.setAccessible(true);
    final Session mailSession = (Session) sessionField.get(adapter);

    // Act
    final PasswordAuthentication auth =
            mailSession.requestPasswordAuthentication(null, PORT, "smtp", "Login", USERNAME);

    // Assert
    assertAll(
            "Verificación de credenciales SMTP",
            () -> assertEquals(USERNAME, auth.getUserName(), "El nombre de usuario no coincide con la configuración"),
            () -> assertEquals(PASSWORD, auth.getPassword(), "La contraseña no coincide con la configuración")
    );
  }
}