package com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception;

/**
 * Excepción personalizada para encapsular errores de la capa de persistencia.
 */
public class PersistenceException extends RuntimeException {

  public PersistenceException(String message) {
    super(message);
  }

  public PersistenceException(String message, Throwable cause) {
    super(message, cause);
  }
}