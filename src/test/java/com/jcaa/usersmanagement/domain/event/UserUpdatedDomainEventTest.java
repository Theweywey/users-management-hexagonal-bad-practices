package com.jcaa.usersmanagement.domain.event;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para UserUpdatedDomainEvent.
 * <p>Verifica que el evento de actualización capture fielmente el estado del usuario,
 * el nombre del evento y la marca de tiempo de ocurrencia.
 * * Clean Code - Regla 11: Documentación y estructura de pruebas (AAA).
 * Clean Code - Regla 10: Uso de constantes para evitar Strings mágicos.
 */
@DisplayName("Pruebas Unitarias: UserUpdatedDomainEvent")
class UserUpdatedDomainEventTest {

  // Regla 10: Centralizamos el nombre del evento para evitar "Strings Mágicos"
  private static final String EXPECTED_EVENT_NAME = "user.updated";

  // Arrange Generales
  private static final String ID = "user-003";
  private static final String NAME = "Jane Doe";
  private static final String EMAIL = "jane.doe@example.com";
  private static final String HASH = "$2a$12$abcdefghijklmnopqrstuO";

  private UserModel user;

  @BeforeEach
  void setUp() {
    user = new UserModel(
            new UserId(ID),
            new UserName(NAME),
            new UserEmail(EMAIL),
            UserPassword.fromHash(HASH),
            UserRole.ADMIN,
            UserStatus.INACTIVE);
  }

  @Test
  @DisplayName("Debe retornar el nombre de evento 'user.updated'")
  void shouldHaveEventNameUserUpdated() {
    // Arrange
    final UserUpdatedDomainEvent event = new UserUpdatedDomainEvent(user);

    // Act
    final String result = event.getEventName();

    // Assert
    assertEquals(EXPECTED_EVENT_NAME, result, "El nombre del evento debe ser consistente con la actualización");
  }

  @Test
  @DisplayName("Debe registrar la fecha de ocurrencia al momento de la construcción")
  void shouldRecordOccurredOnAtCreationTime() {
    // Arrange
    final LocalDateTime before = LocalDateTime.now();
    final UserUpdatedDomainEvent event = new UserUpdatedDomainEvent(user);
    final LocalDateTime after = LocalDateTime.now();

    // Act
    final LocalDateTime occurredOn = event.getOccurredOn();

    // Assert
    assertNotNull(occurredOn, "occurredOn no debe ser null");
    assertFalse(occurredOn.isBefore(before), "La fecha debe ser posterior o igual al inicio del test");
    assertFalse(occurredOn.isAfter(after), "La fecha debe ser anterior o igual al final del test");
  }

  @Test
  @DisplayName("Debe devolver la misma instancia de UserModel que recibió en el constructor")
  void shouldReturnSameUserInstance() {
    // Arrange
    final UserUpdatedDomainEvent event = new UserUpdatedDomainEvent(user);

    // Act
    final UserModel result = event.getUser();

    // Assert
    assertSame(user, result, "Debe ser exactamente la misma instancia de memoria");
  }

  @Test
  @DisplayName("Debe generar un payload con todos los campos del usuario actualizado correctamente")
  void shouldReturnPayloadWithAllUserFields() {
    // Arrange
    final UserUpdatedDomainEvent event = new UserUpdatedDomainEvent(user);

    // Act
    final Map<String, String> payload = event.payload();

    // Assert
    assertAll(
            "Verificación de integridad del payload de actualización",
            () -> assertEquals(5, payload.size(), "El payload debe tener exactamente 5 campos"),
            () -> assertEquals(ID, payload.get("id"), "id incorrecto"),
            () -> assertEquals(NAME, payload.get("name"), "nombre incorrecto"),
            () -> assertEquals(EMAIL, payload.get("email"), "email incorrecto"),
            () -> assertEquals(UserRole.ADMIN.name(), payload.get("role"), "rol incorrecto"),
            () -> assertEquals(UserStatus.INACTIVE.name(), payload.get("status"), "estado incorrecto")
    );
  }
}