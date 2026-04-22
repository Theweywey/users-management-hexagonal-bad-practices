package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.LoginUseCase;
import com.jcaa.usersmanagement.application.port.out.GetUserByEmailPort;
import com.jcaa.usersmanagement.application.service.dto.command.LoginCommand;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.exception.InvalidCredentialsException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public final class LoginService implements LoginUseCase {

  private final GetUserByEmailPort getUserByEmailPort;
  private final Validator validator;

  @Override
  public UserModel execute(final LoginCommand command) {
    validateCommand(command);

    final UserModel user = findUserOrThrow(command.email());
    ensurePasswordMatches(user, command.password());
    ensureUserIsActive(user);

    return user;
  }

  private UserModel findUserOrThrow(final String rawEmail) {
    final UserEmail email = new UserEmail(rawEmail);
    return getUserByEmailPort
            .getByEmail(email)
            .orElseThrow(InvalidCredentialsException::becauseCredentialsAreInvalid);
  }

  private void ensurePasswordMatches(final UserModel user, final String plainPassword) {
    // Se encapsula la verificación para evitar romper la Ley de Deméter en el método principal
    if (!user.getPassword().verifyPlain(plainPassword)) {
      throw InvalidCredentialsException.becauseCredentialsAreInvalid();
    }
  }

  private void ensureUserIsActive(final UserModel user) {
    // Simplificación de condición booleana compleja (Regla 17)
    if (user.getStatus() != UserStatus.ACTIVE) {
      throw InvalidCredentialsException.becauseUserIsNotActive();
    }
  }

  private void validateCommand(final LoginCommand command) {
    final Set<ConstraintViolation<LoginCommand>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}