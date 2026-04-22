package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidUserEmailException;
import java.util.Objects;
import java.util.regex.Pattern;

public record UserEmail(String value) {

  // Centralizamos la "verdad" sobre qué es un email válido aquí (Regla 23)
  private static final Pattern EMAIL_PATTERN =
          Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

  public UserEmail {
    // 1. Normalización
    final String normalizedValue = Objects.requireNonNull(value, "UserEmail cannot be null")
            .trim()
            .toLowerCase();

    // 2. Validaciones
    validateNotEmpty(normalizedValue);
    validateFormat(normalizedValue);

    // 3. Asignación
    value = normalizedValue;
  }

  private static void validateNotEmpty(final String normalizedValue) {
    if (normalizedValue.isEmpty()) {
      throw InvalidUserEmailException.becauseValueIsEmpty();
    }
  }

  private static void validateFormat(final String normalizedValue) {
    if (!EMAIL_PATTERN.matcher(normalizedValue).matches()) {
      throw InvalidUserEmailException.becauseFormatIsInvalid(normalizedValue);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}