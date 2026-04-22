package com.jcaa.usersmanagement.domain.exception;

public final class InvalidUserStatusException extends DomainException {

  // Regla 10: Se extrae el mensaje a una constante privada para evitar hardcoding
  private static final String INVALID_STATUS_MESSAGE = "The user status '%s' is not valid.";

  private InvalidUserStatusException(final String message) {
    super(message);
  }

  public static InvalidUserStatusException becauseValueIsInvalid(final String status) {
    return new InvalidUserStatusException(
            String.format(INVALID_STATUS_MESSAGE, status));
  }
}