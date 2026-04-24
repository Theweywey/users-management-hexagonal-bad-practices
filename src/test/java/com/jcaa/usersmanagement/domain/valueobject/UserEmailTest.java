package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.exception.InvalidUserEmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas unitarias para el Value Object UserEmail.
 * <p>Verifica la integridad de los correos electrónicos mediante:
 * <ul>
 * <li>Normalización (limpieza de espacios y conversión a minúsculas).</li>
 * <li>Validación de formato según estándares de negocio.</li>
 * <li>Protección contra valores nulos o vacíos.</li>
 * </ul>
 * Clean Code - Regla 11: Estructura AAA y uso de pruebas parametrizadas descriptivas.
 */
@DisplayName("Pruebas Unitarias: UserEmail")
class UserEmailTest {

  @Test
  @DisplayName("Debe normalizar el email eliminando espacios y convirtiendo a minúsculas")
  void shouldNormalizeEmail() {
    // Arrange
    final String rawEmail = "  JOHN.ARRIETA@gmail.com  ";
    final String expectedEmail = "john.arrieta@gmail.com";

    // Act
    final UserEmail userEmail = new UserEmail(rawEmail);

    // Assert
    assertEquals(expectedEmail, userEmail.value(), "El email debe estar normalizado (trim y lowercase)");
  }

  @Test
  @DisplayName("Debe lanzar InvalidUserEmailException cuando el email está en blanco")
  void shouldThrowInvalidUserEmailExceptionWhenEmailIsBlank() {
    // Arrange
    final String blankEmail = "   ";

    // Act & Assert
    assertThrows(InvalidUserEmailException.class, () -> new UserEmail(blankEmail),
            "Se esperaba excepción por email con solo espacios");
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando el email es nulo")
  void shouldThrowNullPointerExceptionWhenEmailIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> new UserEmail(null),
            "Se esperaba NPE al pasar un objeto nulo");
  }

  @Test
  @DisplayName("Debe retornar el valor del email al llamar a toString()")
  void shouldReturnEmailValueOnToString() {
    // Arrange
    final String emailValue = "test@example.com";
    final UserEmail userEmail = new UserEmail(emailValue);

    // Act & Assert
    assertEquals(emailValue, userEmail.toString(), "toString() debe retornar el valor bruto del email");
  }

  // ------ Pruebas Parametrizadas (Formatos) --------

  @ParameterizedTest
  @ValueSource(strings = {
          "john.arrieta@gmail.com",
          "john-arrieta_arrieta@gmail.com.co",
          "john1234567arrieta@gmail.com"
  })
  @DisplayName("Debe aceptar formatos de email válidos")
  void shouldAcceptValidEmailFormats(String validEmail) {
    // Act & Assert
    assertDoesNotThrow(() -> new UserEmail(validEmail),
            "El formato de email '" + validEmail + "' debería ser considerado válido");
  }

  @ParameterizedTest
  @ValueSource(strings = {
          "johnarrieta-sin-arroba.com",
          "john.arrieta@gmail",
          "john.arrieta@.com",
          "john arrieta@com",
          "@",
          "email..doblepunto@test.com"
  })
  @DisplayName("Debe lanzar InvalidUserEmailException para formatos de email inválidos")
  void shouldThrowExceptionForInvalidEmailFormats(String invalidEmail) {
    // Act & Assert
    assertThrows(InvalidUserEmailException.class, () -> new UserEmail(invalidEmail),
            "El formato de email '" + invalidEmail + "' debería ser rechazado");
  }
}