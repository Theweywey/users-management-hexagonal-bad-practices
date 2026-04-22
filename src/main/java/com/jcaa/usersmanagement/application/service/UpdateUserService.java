package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.UpdateUserUseCase;
import com.jcaa.usersmanagement.application.port.out.GetUserByEmailPort;
import com.jcaa.usersmanagement.application.port.out.GetUserByIdPort;
import com.jcaa.usersmanagement.application.port.out.UpdateUserPort;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateUserCommand;
import com.jcaa.usersmanagement.application.service.mapper.UserApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.UserAlreadyExistsException;
import com.jcaa.usersmanagement.domain.exception.UserNotFoundException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.Set;

@Log
@RequiredArgsConstructor
public final class UpdateUserService implements UpdateUserUseCase {

  private final UpdateUserPort updateUserPort;
  private final GetUserByIdPort getUserByIdPort;
  private final GetUserByEmailPort getUserByEmailPort;
  private final EmailNotificationService emailNotificationService;
  private final Validator validator;

  @Override
  public UserModel execute(final UpdateUserCommand command) {
    validateCommand(command);
    logUpdateAttempt(command.id());

    final UserId userId = new UserId(command.id());
    final UserModel current = findExistingUserOrFail(userId);
    final UserEmail newEmail = new UserEmail(command.email());

    ensureEmailIsNotTakenByAnotherUser(newEmail, userId);

    final UserModel userToUpdate = UserApplicationMapper.fromUpdateCommandToModel(command, current.getPassword());
    final UserModel updatedUser = updateUserPort.update(userToUpdate);

    emailNotificationService.notifyUserUpdated(updatedUser);

    return updatedUser;
  }

  private void logUpdateAttempt(final String userId) {
    // Se evita registrar PII (nombre o email) en los logs, usando solo el ID.
    log.info(String.format("Iniciando actualización para el usuario con ID: %s", userId));
  }

  private void validateCommand(final UpdateUserCommand command) {
    final Set<ConstraintViolation<UpdateUserCommand>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  private UserModel findExistingUserOrFail(final UserId userId) {
    return getUserByIdPort
            .getById(userId)
            .orElseThrow(() -> UserNotFoundException.becauseIdWasNotFound(userId.value()));
  }

  private void ensureEmailIsNotTakenByAnotherUser(final UserEmail newEmail, final UserId ownerId) {
    // Solución al condicional complejo (Reglas 17, 25, 26, 27):
    // Se consulta la base de datos una sola vez. Si el email existe y no pertenece al usuario actual, falla.
    getUserByEmailPort.getByEmail(newEmail).ifPresent(existingUser -> {
      if (!existingUser.getId().equals(ownerId)) {
        throw UserAlreadyExistsException.becauseEmailAlreadyExists(newEmail.value());
      }
    });
  }
}