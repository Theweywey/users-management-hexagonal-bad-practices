package com.jcaa.usersmanagement.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para UserAlreadyExistsException.
 * <p>Verifica que el mensaje de excepción identifique correctamente el recurso
 * duplicado (email) para facilitar la depuración y la respuesta al usuario.
 * * Clean Code - Regla 11: Estructura AAA y documentación de comportamiento.
 * Clean Code - Regla 10: Uso de constantes para datos de prueba.
 */
@DisplayName("Pruebas Unitarias: UserAlreadyExistsException")
class UserAlreadyExistsExceptionTest {

  // Regla 10: Constante para evitar "Magic Strings" en los tests
  private static final String DUPLICATE_EMAIL = "existing@example.com";

  @Test
  @DisplayName("Debe incluir el email duplicado en el mensaje de error generado")
  void shouldIncludeEmailInMessage() {
    // Arrange
    // El dato de prueba está definido en DUPLICATE_EMAIL

    // Act
    final String message = UserAlreadyExistsException.becauseEmailAlreadyExists(DUPLICATE_EMAIL).getMessage();

    // Assert
    // Regla 11: Aserciones expresivas con mensajes de fallo claros
    assertAll(
            "Verificación de contenido del mensaje de excepción",
            () -> assertNotNull(message, "El mensaje de error no debe ser nulo"),
            () -> assertTrue(message.contains(DUPLICATE_EMAIL),
                    "El mensaje debe contener explícitamente el email que ya existe en el sistema")
    );
  }
}