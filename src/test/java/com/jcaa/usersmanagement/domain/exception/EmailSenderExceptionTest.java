package com.jcaa.usersmanagement.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para EmailSenderException.
 * <p>Verifica que los mensajes de error se formateen correctamente y que la excepción
 * encapsule adecuadamente las causas originales (Throwable).
 * * Clean Code - Regla 11: Estructura AAA y aserciones expresivas.
 * Clean Code - Regla 10: Uso de constantes para datos de prueba.
 */
@DisplayName("Pruebas Unitarias: EmailSenderException")
class EmailSenderExceptionTest {

  // Regla 10: Constantes para evitar Strings mágicos en los tests
  private static final String DESTINATION_EMAIL = "user@example.com";
  private static final String SMTP_ERROR = "Connection refused";
  private static final String IO_ERROR_MSG = "IO error";

  @Test
  @DisplayName("Debe formatear el mensaje incluyendo el email y el error SMTP específico")
  void shouldFormatMessageWithEmailAndSmtpError() {
    // Arrange
    // Los datos provienen de las constantes de clase

    // Act
    final String message =
            EmailSenderException.becauseSmtpFailed(DESTINATION_EMAIL, SMTP_ERROR).getMessage();

    // Assert
    assertAll(
            "Verificación de formato de mensaje SMTP",
            () -> assertTrue(message.contains(DESTINATION_EMAIL), "El mensaje debe incluir el email de destino"),
            () -> assertTrue(message.contains(SMTP_ERROR), "El mensaje debe incluir la descripción del error SMTP")
    );
  }

  @Test
  @DisplayName("Debe encapsular la causa original y producir un mensaje descriptivo")
  void shouldWrapCauseAndProduceNonBlankMessage() {
    // Arrange
    final Throwable cause = new RuntimeException(IO_ERROR_MSG);

    // Act
    final EmailSenderException exception = EmailSenderException.becauseSendFailed(cause);

    // Assert
    assertAll(
            "Verificación de encapsulamiento de causa",
            () -> assertSame(cause, exception.getCause(), "Debe mantener la referencia a la causa original"),
            () -> assertNotNull(exception.getMessage(), "El mensaje no debe ser nulo"),
            () -> assertFalse(exception.getMessage().isBlank(), "El mensaje de error no debe estar vacío")
    );
  }
}