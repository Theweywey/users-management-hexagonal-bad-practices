package com.jcaa.usersmanagement.domain.exception;

public final class InvalidUserNameException extends DomainException {

  // Regla 10: Centralización de mensajes en constantes para evitar hardcoding
  private static final String EMPTY_NAME_MESSAGE = "The user name must not be empty.";
  private static final String NAME_TOO_SHORT_MESSAGE = "The user name must have at least %d characters.";

  private InvalidUserNameException(final String message) {
    super(message);
  }

  public static InvalidUserNameException becauseValueIsEmpty() {
    return new InvalidUserNameException(EMPTY_NAME_MESSAGE);
  }

  public static InvalidUserNameException becauseLengthIsTooShort(final int minimumLength) {
    return new InvalidUserNameException(
            String.format(NAME_TOO_SHORT_MESSAGE, minimumLength));
  }
}