package com.jcaa.usersmanagement.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para UserNotFoundException.
 * <p>Asegura que el mensaje de error contenga el identificador del usuario
 * para facilitar el diagnóstico de errores en las capas superiores.
 * * Clean Code - Regla 11: Estructura AAA y documentación.
 * Clean Code - Regla 10: Uso de constantes para datos de prueba.
 */
@DisplayName("Pruebas Unitarias: UserNotFoundException")
class UserNotFoundExceptionTest {

  // Regla 10: Constante para evitar "Magic Strings"
  private static final String MISSING_USER_ID = "user-404";

  @Test
  @DisplayName("Debe incluir el ID del usuario en el mensaje cuando no se encuentra")
  void shouldIncludeUserIdInMessage() {
    // Arrange
    // El ID se obtiene de la constante MISSING_USER_ID

    // Act
    final String message = UserNotFoundException.becauseIdWasNotFound(MISSING_USER_ID).getMessage();

    // Assert
    // Regla 11: Aserciones expresivas con assertAll
    assertAll(
            "Verificación de contenido del mensaje de excepción",
            () -> assertNotNull(message, "El mensaje no debe ser nulo"),
            () -> assertTrue(message.contains(MISSING_USER_ID),
                    "El mensaje de error debe contener explícitamente el ID del usuario buscado")
    );
  }
}