package com.jcaa.usersmanagement.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para InvalidCredentialsException.
 * <p>Verifica que los métodos de fábrica produzcan mensajes de error únicos
 * y descriptivos para cada escenario de fallo en la autenticación.
 * * Clean Code - Regla 11: Estructura AAA y documentación de comportamiento.
 */
@DisplayName("Pruebas Unitarias: InvalidCredentialsException")
class InvalidCredentialsExceptionTest {

  @Test
  @DisplayName("Debe producir mensajes distintos y no vacíos para cada escenario de fallo")
  void shouldProduceDistinctNonBlankMessagesForEachAuthFailureScenario() {
    // Arrange
    // En este caso, la preparación es implícita al llamar a los métodos estáticos

    // Act
    final String invalidCredsMsg =
            InvalidCredentialsException.becauseCredentialsAreInvalid().getMessage();
    final String inactiveUserMsg =
            InvalidCredentialsException.becauseUserIsNotActive().getMessage();

    // Assert
    assertAll(
            "Verificación de mensajes de autenticación",
            () -> assertNotNull(invalidCredsMsg, "El mensaje de credenciales inválidas no debe ser nulo"),
            () -> assertFalse(invalidCredsMsg.isBlank(), "El mensaje de credenciales inválidas no debe estar vacío"),

            () -> assertNotNull(inactiveUserMsg, "El mensaje de usuario inactivo no debe ser nulo"),
            () -> assertFalse(inactiveUserMsg.isBlank(), "El mensaje de usuario inactivo no debe estar vacío"),

            () -> assertNotEquals(invalidCredsMsg, inactiveUserMsg,
                    "Cada escenario debe tener su propio mensaje para ayudar a identificar la causa del fallo")
    );
  }
}