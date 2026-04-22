package com.jcaa.usersmanagement.domain.exception;

public final class UserNotFoundException extends DomainException {

  // Regla 10: Centralización del mensaje en una constante para evitar hardcoding
  private static final String USER_NOT_FOUND_MESSAGE = "The user with id '%s' was not found.";

  private UserNotFoundException(final String message) {
    super(message);
  }

  public static UserNotFoundException becauseIdWasNotFound(final String userId) {
    return new UserNotFoundException(
            String.format(USER_NOT_FOUND_MESSAGE, userId));
  }
}