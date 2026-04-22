package com.jcaa.usersmanagement.infrastructure.adapter.persistence.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.PersistenceException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para la fábrica de conexiones de base de datos.
 * Verifica la creación exitosa de conexiones y el manejo de excepciones SQL.
 *
 * Clean Code - Regla 11: Se añade Javadoc descriptivo a la clase.
 * Clean Code - Regla 4: Se eliminan las instancias de la fábrica al ser una @UtilityClass.
 */
@DisplayName("DatabaseConnectionFactory")
@ExtendWith(MockitoExtension.class)
class DatabaseConnectionFactoryTest {

  private static final String HOST = "localhost";
  private static final int PORT = 3306;
  private static final String DB_NAME = "test_db";
  private static final String USERNAME = "test_user";
  private static final String PASSWORD = "test_pass";

  @Mock
  private Connection mockConnection;

  private DatabaseConfig config;

  @BeforeEach
  void setUp() {
    config = new DatabaseConfig(HOST, PORT, DB_NAME, USERNAME, PASSWORD);
    // Ya no instanciamos la fábrica (Regla 4)
  }

  @Test
  @DisplayName("createConnection() returns the connection provided by DriverManager")
  void shouldReturnConnectionWhenDriverManagerSucceeds() {
    try (final MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
      mockedDriverManager
              .when(() -> DriverManager.getConnection(any(), any(), any()))
              .thenReturn(mockConnection);

      // Llamada estática directa a la clase (Regla 4)
      final Connection result = DatabaseConnectionFactory.createConnection(config);

      assertSame(mockConnection, result, "must return the connection provided by DriverManager");
    }
  }

  @Test
  @DisplayName("createConnection() throws PersistenceException when DriverManager fails")
  void shouldThrowPersistenceExceptionWhenDriverManagerFails() {
    final SQLException cause = new SQLException("Connection refused");
    try (final MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
      mockedDriverManager
              .when(() -> DriverManager.getConnection(any(), any(), any()))
              .thenThrow(cause);

      assertThrows(
              PersistenceException.class,
              () -> DatabaseConnectionFactory.createConnection(config),
              "must throw PersistenceException when DriverManager throws SQLException");
    }
  }
}