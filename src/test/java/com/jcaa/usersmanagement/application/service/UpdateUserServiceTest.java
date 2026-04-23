package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetUserByEmailPort;
import com.jcaa.usersmanagement.application.port.out.GetUserByIdPort;
import com.jcaa.usersmanagement.application.port.out.UpdateUserPort;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateUserCommand;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.exception.UserAlreadyExistsException;
import com.jcaa.usersmanagement.domain.exception.UserNotFoundException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para UpdateUserService.
 * * <p>Esta suite verifica la lógica de actualización de usuarios, incluyendo:
 * <ul>
 * <li>Flujo de éxito con notificación.</li>
 * <li>Manejo de errores por ID inexistente.</li>
 * <li>Validación de conflictos de email (unicidad).</li>
 * <li>Permisividad cuando el email no cambia (mismo usuario).</li>
 * <li>Validación de integridad del comando de entrada.</li>
 * </ul>
 * Clean Code - Regla 11: Documentación de casos, estructura AAA y aserciones expresivas.
 */
@DisplayName("Pruebas Unitarias: UpdateUserService")
@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {

  @Mock private UpdateUserPort updateUserPort;
  @Mock private GetUserByIdPort getUserByIdPort;
  @Mock private GetUserByEmailPort getUserByEmailPort;
  @Mock private EmailNotificationService emailNotificationService;

  private UpdateUserService service;

  private static final String ID = "u-001";
  private static final String EMAIL = "john@example.com";
  private static final String HASH = "$2a$12$abcdefghijklmnopqrstuO";

  private UserModel existingUser;

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service =
              new UpdateUserService(
                      updateUserPort,
                      getUserByIdPort,
                      getUserByEmailPort,
                      emailNotificationService,
                      validatorFactory.getValidator());
    }

    existingUser = createTestUser(ID, EMAIL);
  }

  @Test
  @DisplayName("Debe actualizar el usuario y notificar cuando los datos son válidos")
  void shouldUpdateUserAndNotifyWhenDataIsValid() {
    // Arrange
    final UpdateUserCommand command =
            new UpdateUserCommand(ID, "John Updated", EMAIL, null, "ADMIN", "ACTIVE");

    when(getUserByIdPort.getById(any())).thenReturn(Optional.of(existingUser));
    when(getUserByEmailPort.getByEmail(any())).thenReturn(Optional.of(existingUser));
    when(updateUserPort.update(any())).thenReturn(existingUser);

    // Act
    final UserModel result = service.execute(command);

    // Assert
    // Regla 11: Aserciones expresivas
    assertNotNull(result, "El usuario actualizado no debe ser nulo");
    verify(updateUserPort).update(any(UserModel.class));
    verify(emailNotificationService).notifyUserUpdated(existingUser);
  }

  @Test
  @DisplayName("Debe lanzar UserNotFoundException cuando el ID del usuario no existe")
  void shouldThrowWhenUserNotFound() {
    // Arrange
    final UpdateUserCommand command =
            new UpdateUserCommand("no-existe", "Name", "a@b.com", null, "MEMBER", "ACTIVE");
    when(getUserByIdPort.getById(any())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> service.execute(command));
    verify(updateUserPort, never()).update(any());
  }

  @Test
  @DisplayName("Debe lanzar UserAlreadyExistsException cuando el email pertenece a otro usuario")
  void shouldThrowWhenEmailBelongsToAnotherUser() {
    // Arrange
    final String otherEmail = "other@example.com";
    final UpdateUserCommand command =
            new UpdateUserCommand(ID, "John", otherEmail, null, "MEMBER", "ACTIVE");

    final UserModel otherUser = createTestUser("u-999", otherEmail);

    when(getUserByIdPort.getById(any())).thenReturn(Optional.of(existingUser));
    when(getUserByEmailPort.getByEmail(any())).thenReturn(Optional.of(otherUser));

    // Act & Assert
    assertThrows(UserAlreadyExistsException.class, () -> service.execute(command));
    verify(updateUserPort, never()).update(any());
  }

  @Test
  @DisplayName("Debe permitir mantener el mismo email sin lanzar excepciones")
  void shouldAllowKeepingOwnEmail() {
    // Arrange
    final UpdateUserCommand command =
            new UpdateUserCommand(ID, "John Updated", EMAIL, null, "ADMIN", "ACTIVE");

    when(getUserByIdPort.getById(any())).thenReturn(Optional.of(existingUser));
    when(getUserByEmailPort.getByEmail(any())).thenReturn(Optional.of(existingUser));
    when(updateUserPort.update(any())).thenReturn(existingUser);

    // Act & Assert
    assertDoesNotThrow(() -> service.execute(command));
    verify(updateUserPort).update(any());
  }

  @Test
  @DisplayName("Debe lanzar ConstraintViolationException cuando el comando tiene datos inválidos")
  void shouldThrowWhenCommandIsInvalid() {
    // Arrange
    final UpdateUserCommand command =
            new UpdateUserCommand("", "Jo", "no-es-email", null, "MEMBER", "ACTIVE");

    // Act & Assert
    assertThrows(ConstraintViolationException.class, () -> service.execute(command));
    verifyNoInteractions(updateUserPort, getUserByIdPort, getUserByEmailPort);
  }

  // Método helper para reducir ruido en los tests (Regla 11)
  private UserModel createTestUser(String id, String email) {
    return new UserModel(
            new UserId(id),
            new UserName("Test User"),
            new UserEmail(email),
            UserPassword.fromHash(HASH),
            UserRole.MEMBER,
            UserStatus.ACTIVE);
  }
}