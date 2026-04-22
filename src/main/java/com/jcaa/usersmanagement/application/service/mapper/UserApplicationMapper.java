package com.jcaa.usersmanagement.application.service.mapper;

import com.jcaa.usersmanagement.application.service.dto.command.CreateUserCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteUserCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateUserCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetUserByIdQuery;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;

public class UserApplicationMapper {

  public static UserModel fromCreateCommandToModel(final CreateUserCommand command) {
    // Regla 24: Nombre consistente (email)
    final String email = command.email();

    return UserModel.create(
            new UserId(command.id()),
            new UserName(command.name()),
            new UserEmail(email),
            UserPassword.fromPlainText(command.password()),
            UserRole.fromString(command.role()));
  }

  public static UserModel fromUpdateCommandToModel(
          final UpdateUserCommand command,
          final UserPassword currentPassword) {

    final UserPassword passwordToUse = (command.password() == null || command.password().isBlank())
            ? currentPassword
            : UserPassword.fromPlainText(command.password());

    // Regla 24: Mantenemos el nombre 'email' para ser consistentes con el método de arriba
    final String email = command.email();

    return new UserModel(
            new UserId(command.id()),
            new UserName(command.name()),
            new UserEmail(email),
            passwordToUse,
            UserRole.fromString(command.role()),
            UserStatus.fromString(command.status()));
  }

  public static UserId fromGetUserByIdQueryToUserId(final GetUserByIdQuery query) {
    return new UserId(query.id());
  }

  public static UserId fromDeleteCommandToUserId(final DeleteUserCommand command) {
    return new UserId(command.id());
  }

  /**
   * Regla 21: Se eliminan los códigos de error (-1).
   * Si el rol es inválido, lanzamos una excepción clara en lugar de retornar un número mágico.
   */
  public static int roleToCode(final String role) {
    if (role == null || role.isBlank()) {
      throw new IllegalArgumentException("Role cannot be null or empty");
    }

    return switch (role.toUpperCase()) {
      case "ADMIN" -> 1;
      case "MEMBER" -> 2;
      case "REVIEWER" -> 3;
      default -> throw new IllegalArgumentException("Unknown role: " + role);
    };
  }
}