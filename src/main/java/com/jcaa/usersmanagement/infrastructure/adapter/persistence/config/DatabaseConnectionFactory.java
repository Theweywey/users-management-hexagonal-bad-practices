package com.jcaa.usersmanagement.infrastructure.adapter.persistence.config;

import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.PersistenceException;
import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clean Code - Regla 4: Uso de @UtilityClass para clases de herramientas.
 * Al usar esta anotación, Lombok hace que la clase sea final, genera un constructor
 * privado para evitar instanciación y convierte todos los métodos en estáticos.
 */
@UtilityClass
public class DatabaseConnectionFactory {

  public static Connection createConnection(final DatabaseConfig config) {
    try {
      return DriverManager.getConnection(
              config.buildJdbcUrl(),
              config.username(),
              config.password()
      );
    } catch (final SQLException exception) {
      throw PersistenceException.becauseConnectionFailed(exception);
    }
  }
}