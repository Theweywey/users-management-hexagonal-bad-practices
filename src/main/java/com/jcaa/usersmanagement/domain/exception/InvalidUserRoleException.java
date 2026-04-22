package com.jcaa.usersmanagement.domain.exception;

public final class InvalidUserRoleException extends DomainException {

  // Regla 10: Definición de constante para evitar el hardcoding del mensaje
  private static final String INVALID_ROLE_MESSAGE = "The user role '%s' is not valid.";

  private InvalidUserRoleException(final String message) {
    super(message);
  }

  public static InvalidUserRoleException becauseValueIsInvalid(final String role) {
    return new InvalidUserRoleException(
            String.format(INVALID_ROLE_MESSAGE, role));
  }
}