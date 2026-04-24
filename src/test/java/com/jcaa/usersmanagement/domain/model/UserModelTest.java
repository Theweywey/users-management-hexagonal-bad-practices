package com.jcaa.usersmanagement.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para UserModel.
 * <p>Verifica el ciclo de vida del usuario y sus transiciones de estado:
 * <ul>
 * <li>Creación inicial con estado PENDING.</li>
 * <li>Activación del usuario (Inmutabilidad y cambio a ACTIVE).</li>
 * <li>Desactivación del usuario (Inmutabilidad y cambio a INACTIVE).</li>
 * </ul>
 * Clean Code - Regla 11: Estructura AAA y validación de inmutabilidad.
 * Clean Code - Regla 10: Centralización de constantes de prueba.
 */
@DisplayName("Pruebas Unitarias: UserModel")
class UserModelTest {

  private static final String TEST_HASH = "$2a$12$abcdefghijklmnopqrstuO";
  private static final String TEST_ID = "u-001";

  private UserId userId;
  private UserName userName;
  private UserEmail userEmail;
  private UserPassword password;

  @BeforeEach
  void setUp() {
    userId = new UserId(TEST_ID);
    userName = new UserName("Alice Smith");
    userEmail = new UserEmail("alice@example.com");
    password = UserPassword.fromHash(TEST_HASH);
  }

  @Test
  @DisplayName("Debe crear un usuario con estado PENDING y preservar sus campos")
  void shouldCreateUserWithPendingStatusAndPreserveAllFields() {
    // Arrange
    final UserRole role = UserRole.MEMBER;

    // Act
    final UserModel model = UserModel.create(userId, userName, userEmail, password, role);

    // Assert
    assertAll(
            "Verificación de creación de usuario",
            () -> assertEquals(UserStatus.PENDING, model.getStatus(), "El estado inicial debe ser PENDING"),
            () -> assertSame(password, model.getPassword(), "El password debe ser la misma instancia recibida"),
            () -> assertEquals(role, model.getRole(), "El rol debe coincidir")
    );
  }

  @Test
  @DisplayName("Debe activar al usuario retornando una nueva instancia en estado ACTIVE")
  void shouldActivateAndPreserveOtherFields() {
    // Arrange
    final UserModel pending = UserModel.create(userId, userName, userEmail, password, UserRole.REVIEWER);

    // Act
    final UserModel activated = pending.activate();

    // Assert
    assertAll(
            "Verificación de activación (Inmutabilidad)",
            () -> assertNotSame(pending, activated, "Debe retornar una instancia nueva (Inmutabilidad)"),
            () -> assertEquals(UserStatus.ACTIVE, activated.getStatus(), "El nuevo estado debe ser ACTIVE"),
            () -> assertSame(userId, activated.getId(), "El ID debe preservarse por referencia"),
            () -> assertSame(userName, activated.getName(), "El nombre debe preservarse por referencia"),
            () -> assertEquals(UserRole.REVIEWER, activated.getRole(), "El rol debe permanecer igual")
    );
  }

  @Test
  @DisplayName("Debe desactivar al usuario retornando una nueva instancia en estado INACTIVE")
  void shouldDeactivateAndPreserveOtherFields() {
    // Arrange
    final UserModel active = new UserModel(userId, userName, userEmail, password, UserRole.ADMIN, UserStatus.ACTIVE);

    // Act
    final UserModel deactivated = active.deactivate();

    // Assert
    assertAll(
            "Verificación de desactivación (Inmutabilidad)",
            () -> assertNotSame(active, deactivated, "Debe retornar una instancia nueva"),
            () -> assertEquals(UserStatus.INACTIVE, deactivated.getStatus(), "El nuevo estado debe ser INACTIVE"),
            () -> assertSame(userId, deactivated.getId(), "El ID debe ser el mismo"),
            () -> assertEquals(UserRole.ADMIN, deactivated.getRole(), "El rol no debe cambiar")
    );
  }
}