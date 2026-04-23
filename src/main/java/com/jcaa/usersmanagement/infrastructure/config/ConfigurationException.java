package com.jcaa.usersmanagement.infrastructure.config;

public final class ConfigurationException extends RuntimeException {

  // Regla 10: Definición de constante para evitar mensajes hardcodeados
  private static final String LOAD_FAILED_MESSAGE = "Failed to load the application configuration.";

  private ConfigurationException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public static ConfigurationException becauseLoadFailed(final Throwable cause) {
    return new ConfigurationException(LOAD_FAILED_MESSAGE, cause);
  }
}