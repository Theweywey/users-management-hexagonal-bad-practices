package com.jcaa.usersmanagement.domain.event;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.valueobject.UserId;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para UserDeletedDomainEvent.
 * <p>Verifica que el evento de eliminación capture correctamente el identificador
 * del usuario, registre el instante de ocurrencia y genere un payload válido.
 * * Clean Code - Regla 11: Documentación y estructura de pruebas.
 * Clean Code - Regla 10: Uso de constantes para evitar Strings mágicos.
 */
@DisplayName("Pruebas Unitarias: UserDeletedDomainEvent")
class UserDeletedDomainEventTest {

  // Regla 10: Centralizamos valores para evitar duplicidad y Strings mágicos
  private static final String DELETED_USER_ID = "user-002";
  private static final String EXPECTED_EVENT_NAME = "user.deleted";

  @Test
  @DisplayName("Debe retornar el nombre de evento 'user.deleted'")
  void shouldHaveEventNameUserDeleted() {
    // Arrange
    final UserDeletedDomainEvent event = new UserDeletedDomainEvent(new UserId(DELETED_USER_ID));

    // Act
    final String result = event.getEventName();

    // Assert
    assertEquals(EXPECTED_EVENT_NAME, result, "El nombre del evento debe coincidir con la constante definida");
  }

  @Test
  @DisplayName("Debe registrar la fecha de ocurrencia al momento de la construcción")
  void shouldRecordOccurredOnAtCreationTime() {
    // Arrange
    final LocalDateTime before = LocalDateTime.now();
    final UserDeletedDomainEvent event = new UserDeletedDomainEvent(new UserId(DELETED_USER_ID));
    final LocalDateTime after = LocalDateTime.now();

    // Act
    final LocalDateTime occurredOn = event.getOccurredOn();

    // Assert
    assertNotNull(occurredOn, "occurredOn no debe ser null");
    assertFalse(occurredOn.isBefore(before), "La fecha debe ser posterior o igual al inicio de la prueba");
    assertFalse(occurredOn.isAfter(after), "La fecha debe ser anterior o igual al final de la prueba");
  }

  @Test
  @DisplayName("Debe devolver la misma instancia de UserId que recibió en el constructor")
  void shouldReturnSameUserIdInstance() {
    // Arrange
    final UserId userId = new UserId(DELETED_USER_ID);
    final UserDeletedDomainEvent event = new UserDeletedDomainEvent(userId);

    // Act
    final UserId result = event.getUserId();

    // Assert
    // Regla 11: assertSame es más preciso que assertEquals para referencias de objetos
    assertSame(userId, result, "Debe retornar exactamente la misma instancia de UserId");
  }

  @Test
  @DisplayName("Debe generar un payload que contenga únicamente el ID del usuario")
  void shouldReturnPayloadWithOnlyUserId() {
    // Arrange
    final UserDeletedDomainEvent event = new UserDeletedDomainEvent(new UserId(DELETED_USER_ID));

    // Act
    final Map<String, String> payload = event.payload();

    // Assert
    assertAll(
            "Verificación de integridad del payload",
            () -> assertEquals(1, payload.size(), "El payload debe tener exactamente una entrada"),
            () -> assertEquals(DELETED_USER_ID, payload.get("id"), "El valor del ID en el payload debe ser correcto")
    );
  }
}