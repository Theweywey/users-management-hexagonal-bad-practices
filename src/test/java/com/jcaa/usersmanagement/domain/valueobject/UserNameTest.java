package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.exception.InvalidUserNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas unitarias para el Value Object UserName.
 * <p>Verifica que los nombres de usuario:
 * <ul>
 * <li>Sean normalizados (sin espacios extra).</li>
 * <li>Cumplan con una longitud mínima de 3 caracteres.</li>
 * <li>No permitan valores nulos o vacíos.</li>
 * </ul>
 * Clean Code - Regla 11: Documentación con @DisplayName y estructura AAA.
 */
@DisplayName("Pruebas Unitarias: UserName")
class UserNameTest {

  @ParameterizedTest
  @ValueSource(strings = {"John Arrieta", "   John Arrieta   ", "John Arrieta \t"})
  @DisplayName("Debe normalizar el nombre eliminando espacios y caracteres de escape")
  void shouldNormalizeUserName(final String input) {
    // Arrange
    final String expectedValue = "John Arrieta";

    // Act
    final UserName userNameVo = new UserName(input);

    // Assert
    // Regla 11: Uso de assertEquals para mensajes de error más claros
    assertEquals(expectedValue, userNameVo.value(),
            "El nombre de usuario no fue normalizado correctamente");
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando el nombre es nulo")
  void shouldThrowNullPointerExceptionWhenUserNameIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> new UserName(null),
            "Se esperaba NPE al pasar un nombre nulo");
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "\t", "Jo", "Ty  ", "Ed\t"})
  @DisplayName("Debe lanzar InvalidUserNameException para nombres vacíos o menores a 3 caracteres")
  void shouldThrowInvalidUserNameExceptionWhenNameIsTooShortOrEmpty(final String input) {
    // Act & Assert
    assertThrows(InvalidUserNameException.class, () -> new UserName(input),
            "Se esperaba InvalidUserNameException para el valor: '" + input + "'");
  }
}