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
 * Pruebas unitarias para UserCreatedDomainEvent.
 * <p>Verifica que el evento de dominio capture correctamente el estado del usuario,
 * el nombre del evento y la marca de tiempo de ocurrencia.
 * * Clean Code - Regla 11: Estructura AAA y documentación.
 * Clean Code - Regla 10: Uso de constantes para evitar Strings mágicos.
 */
@DisplayName("Pruebas Unitarias: UserCreatedDomainEvent")
class UserCreatedDomainEventTest {

  // Regla 10: Centralizamos el nombre del evento para evitar "Strings Mágicos"
  private static final String EXPECTED_EVENT_NAME = "user.created";

  // Arrange Generales (Regla 4: Corregido typo 'Arrenge')
  private static final String ID = "user-001";
  private static final String NAME = "John Arrieta";
  private static final String EMAIL = "john.arrieta@gmail.com";
  private static final String HASH = "$2a$12$abcdefghijklmnopqrstuO";

  private UserModel user;

  @BeforeEach
  void setUp() {
    user = new UserModel(
            new UserId(ID),
            new UserName(NAME),
            new UserEmail(EMAIL),
            UserPassword.fromHash(HASH),
            UserRole.MEMBER,
            UserStatus.ACTIVE);
  }

  @Test
  @DisplayName("Debe retornar el nombre de evento 'user.created'")
  void shouldHaveEventNameUserCreated() {
    // Arrange
    final UserCreatedDomainEvent event = new UserCreatedDomainEvent(user);

    // Act
    final String result = event.getEventName();

    // Assert
    assertEquals(EXPECTED_EVENT_NAME, result, "El nombre del evento debe ser consistente");
  }

  @Test
  @DisplayName("Debe registrar la fecha de ocurrencia al momento de la construcción")
  void shouldRecordOccurredOnAtCreationTime() {
    // Arrange
    final LocalDateTime before = LocalDateTime.now();
    final UserCreatedDomainEvent event = new UserCreatedDomainEvent(user);
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
    final UserCreatedDomainEvent event = new UserCreatedDomainEvent(user);

    // Act
    final UserModel result = event.getUser();

    // Assert
    assertSame(user, result, "Debe ser la misma instancia de memoria");
  }

  @Test
  @DisplayName("Debe generar un payload con los cinco campos del usuario correctamente")
  void shouldReturnPayloadWithAllUserFields() {
    // Arrange
    final UserCreatedDomainEvent event = new UserCreatedDomainEvent(user);

    // Act
    final Map<String, String> payload = event.payload();

    // Assert
    assertAll(
            "Verificación de campos en el payload",
            () -> assertEquals(5, payload.size(), "El payload debe tener exactamente 5 campos"),
            () -> assertEquals(ID, payload.get("id")),
            () -> assertEquals(NAME, payload.get("name")),
            () -> assertEquals(EMAIL, payload.get("email")),
            () -> assertEquals(UserRole.MEMBER.name(), payload.get("role")),
            () -> assertEquals(UserStatus.ACTIVE.name(), payload.get("status"))
    );
  }
}