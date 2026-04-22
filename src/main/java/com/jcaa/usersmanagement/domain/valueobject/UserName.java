package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidUserNameException;
import java.util.Objects;

public record UserName(String value) {

  // Regla 10: Se recupera la constante para evitar Magic Numbers
  private static final int MINIMUM_LENGTH = 3;

  public UserName {
    // Regla 4: Uso de utilidades estándar para validación de nulos
    final String normalizedValue = Objects.requireNonNull(value, "UserName cannot be null").trim();

    validateNotEmpty(normalizedValue);
    validateMinimumLength(normalizedValue);

    // Asignación del valor limpio
    value = normalizedValue;
  }

  private static void validateNotEmpty(final String normalizedValue) {
    if (normalizedValue.isEmpty()) {
      throw InvalidUserNameException.becauseValueIsEmpty();
    }
  }

  private static void validateMinimumLength(final String normalizedValue) {
    // Uso de la constante en lugar del número literal (Regla 10)
    if (normalizedValue.length() < MINIMUM_LENGTH) {
      throw InvalidUserNameException.becauseLengthIsTooShort(MINIMUM_LENGTH);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}