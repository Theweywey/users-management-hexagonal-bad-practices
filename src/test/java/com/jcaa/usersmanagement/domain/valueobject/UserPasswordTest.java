package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.jcaa.usersmanagement.domain.exception.InvalidUserPasswordException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas unitarias para el Value Object UserPassword.
 * <p>Verifica la seguridad de las contraseñas mediante:
 * <ul>
 * <li>Normalización y hashing seguro.</li>
 * <li>Validación de longitud mínima (8 caracteres).</li>
 * <li>Verificación de contraseñas en texto plano contra hashes.</li>
 * <li>Contrato de igualdad (equals/hashCode) basado en el hash.</li>
 * </ul>
 * Clean Code - Regla 11: Documentación con @DisplayName y estructura AAA.
 */
@DisplayName("Pruebas Unitarias: UserPassword")
class UserPasswordTest {

  @ParameterizedTest
  @ValueSource(strings = {"password123", "   password123   "})
  @DisplayName("Debe normalizar y generar un hash para la contraseña")
  void shouldNormalizeAndHashPassword(final String input) {
    // Arrange
    // El input se recibe de la fuente parametrizada

    // Act
    final UserPassword result = UserPassword.fromPlainText(input);

    // Assert
    // Regla 11: Uso de assertNotNull en lugar de assertTrue(!= null)
    assertNotNull(result.value(), "El hash generado no debe ser nulo");
    assertNotEquals(input.trim(), result.value(), "El valor almacenado debe ser un hash, no texto plano");
  }

  @ParameterizedTest
  @ValueSource(strings = {"clave", "    clave     "})
  @DisplayName("Debe lanzar InvalidUserPasswordException cuando la clave es menor a 8 caracteres")
  void shouldFailWhenPasswordIsTooShort(final String password) {
    // Act & Assert
    assertThrows(InvalidUserPasswordException.class, () -> UserPassword.fromPlainText(password),
            "Se esperaba excepción por contraseña demasiado corta");
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "\t", "\n"})
  @DisplayName("Debe lanzar InvalidUserPasswordException cuando la clave está vacía o en blanco")
  void shouldThrowWhenPasswordIsEmptyOrBlank(final String password) {
    // Act & Assert
    assertThrows(InvalidUserPasswordException.class, () -> UserPassword.fromPlainText(password));
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando la clave es nula")
  void shouldThrowWhenPasswordIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> UserPassword.fromPlainText(null));
  }

  @Test
  @DisplayName("Debe verificar correctamente la contraseña original contra el hash generado")
  void shouldVerifyPlainPassword() {
    // Arrange
    final String plainPassword = "mySecurePassword";
    final UserPassword userPassword = UserPassword.fromPlainText(plainPassword);

    // Act
    final boolean isValid = userPassword.verifyPlain(plainPassword);

    // Assert
    assertTrue(isValid, "La verificación debería ser exitosa para la contraseña correcta");
  }

  @Test
  @DisplayName("Debe permitir reconstruir un UserPassword desde un hash existente")
  void shouldCreateUserPasswordFromExistingHash() {
    // Arrange
    final String rawPassword = "Abcde1234567";
    final UserPassword originalUserPassword = UserPassword.fromPlainText(rawPassword);
    final String generatedHash = originalUserPassword.value();

    // Act
    final UserPassword fromHashUserPassword = UserPassword.fromHash(generatedHash);

    // Assert
    assertAll("Verificación de reconstrucción desde hash",
            () -> assertEquals(originalUserPassword, fromHashUserPassword,
                    "Los objetos deben ser iguales al compartir el mismo hash"),
            () -> assertTrue(fromHashUserPassword.verifyPlain(rawPassword),
                    "El objeto reconstruido debe ser capaz de verificar el texto plano original")
    );
  }

  @Test
  @DisplayName("equals: Debe retornar false cuando el objeto no es instancia de UserPassword")
  void shouldReturnFalseWhenOtherIsNotInstanceOfUserPassword() {
    // Arrange
    final UserPassword password = UserPassword.fromPlainText("MiPassword123");
    final Object nonUserPassword = mock(Object.class);

    // Act
    final boolean result = password.equals(nonUserPassword);

    // Assert
    assertFalse(result, "Un UserPassword no puede ser igual a un objeto de otra clase");
  }

  @Test
  @DisplayName("hashCode: Debe ser consistente para la misma instancia")
  void shouldReturnConsistentHashCode() {
    // Arrange
    final UserPassword password = UserPassword.fromPlainText("MiPassword123");

    // Act
    final int firstHashCode = password.hashCode();
    final int secondHashCode = password.hashCode();

    // Assert
    assertEquals(firstHashCode, secondHashCode, "El hashCode debe ser estable en la misma instancia");
  }

  @Test
  @DisplayName("hashCode: Objetos iguales deben tener el mismo hashCode")
  void shouldHaveSameHashCodeWhenEqual() {
    // Arrange
    final UserPassword a = UserPassword.fromPlainText("MiPassword123");
    final UserPassword b = UserPassword.fromHash(a.value());

    // Assert
    assertAll("Contrato equals/hashCode",
            () -> assertEquals(a, b, "Los objetos con el mismo hash deben ser iguales"),
            () -> assertEquals(a.hashCode(), b.hashCode(), "Objetos iguales deben tener hashCode idéntico")
    );
  }
}