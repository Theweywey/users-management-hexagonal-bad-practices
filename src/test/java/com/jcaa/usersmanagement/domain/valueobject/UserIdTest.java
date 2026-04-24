package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.exception.InvalidUserIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas unitarias para el Value Object UserId.
 * <p>Verifica que los identificadores de usuario sean únicos, inmutables
 * y que cumplan con las reglas de normalización (trim).
 * * Clean Code - Regla 11: Documentación con @DisplayName y estructura AAA.
 */
@DisplayName("Pruebas Unitarias: UserId")
class UserIdTest {

  @ParameterizedTest
  @ValueSource(strings = {" user123 ", "  user123  ", "user123\t"})
  @DisplayName("Debe normalizar el ID eliminando espacios y caracteres de escape")
  void shouldCreateUserIdWithTrimmedValue(String input) {
    // Arrange
    final String expectedValue = "user123";

    // Act
    final UserId userId = new UserId(input);

    // Assert
    // Regla 11: Uso de assertEquals en lugar de assertTrue(equals)
    assertEquals(expectedValue, userId.value(),
            "El valor del ID no fue normalizado correctamente");
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando el ID es nulo")
  void shouldThrowNullPointerExceptionWhenUserIdIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> new UserId(null),
            "Se esperaba NPE al pasar un ID nulo");
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\t", "\n", "\r"})
  @DisplayName("Debe lanzar InvalidUserIdException cuando el ID está vacío o solo contiene espacios")
  void shouldThrowInvalidUserIdExceptionWhenUserIdIsEmpty(String input) {
    // Act & Assert
    assertThrows(InvalidUserIdException.class, () -> new UserId(input),
            "Se esperaba InvalidUserIdException para un ID vacío o en blanco");
  }
}