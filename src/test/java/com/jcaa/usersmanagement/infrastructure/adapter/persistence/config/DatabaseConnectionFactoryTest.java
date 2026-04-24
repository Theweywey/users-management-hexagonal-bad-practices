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
 * Pruebas unitarias para DatabaseConnectionFactory.
 * <p>Verifica la correcta integración con el DriverManager de JDBC, cubriendo:
 * <ul>
 * <li>Creación exitosa de conexiones a partir de la configuración.</li>
 * <li>Manejo de errores y envoltura de SQLException en PersistenceException.</li>
 * </ul>
 * Clean Code - Regla 11: Documentación de casos y estructura AAA.
 * Clean Code - Regla 4: Acceso estático a la fábrica (@UtilityClass).
 */
@DisplayName("Pruebas Unitarias: DatabaseConnectionFactory")
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
  }

  @Test
  @DisplayName("Debe retornar la conexión cuando DriverManager se conecta con éxito")
  void shouldReturnConnectionWhenDriverManagerSucceeds() {
    // Arrange
    try (final MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
      mockedDriverManager
              .when(() -> DriverManager.getConnection(any(), any(), any()))
              .thenReturn(mockConnection);

      // Act
      final Connection result = DatabaseConnectionFactory.createConnection(config);

      // Assert
      assertSame(mockConnection, result, "Debe retornar exactamente la instancia provista por el driver");
    }
  }

  @Test
  @DisplayName("Debe lanzar PersistenceException cuando falla la conexión JDBC")
  void shouldThrowPersistenceExceptionWhenDriverManagerFails() {
    // Arrange
    final SQLException cause = new SQLException("Connection refused");
    try (final MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
      mockedDriverManager
              .when(() -> DriverManager.getConnection(any(), any(), any()))
              .thenThrow(cause);

      // Act & Assert
      assertThrows(
              PersistenceException.class,
              () -> DatabaseConnectionFactory.createConnection(config),
              "Se esperaba PersistenceException ante un error de red o credenciales");
    }
  }
}