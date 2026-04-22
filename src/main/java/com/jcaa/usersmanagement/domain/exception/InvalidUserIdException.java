package com.jcaa.usersmanagement.domain.exception;

public final class InvalidUserIdException extends DomainException {

  // Regla 10: Centralización del mensaje de error en una constante
  private static final String EMPTY_ID_MESSAGE = "The user id must not be empty.";

  private InvalidUserIdException(final String message) {
    super(message);
  }

  public static InvalidUserIdException becauseValueIsEmpty() {
    return new InvalidUserIdException(EMPTY_ID_MESSAGE);
  }
}