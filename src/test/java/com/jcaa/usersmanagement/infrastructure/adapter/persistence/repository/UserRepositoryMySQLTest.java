package com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.exception.UserNotFoundException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.PersistenceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para UserRepositoryMySQL.
 * <p>Esta suite valida la integridad de la persistencia en MySQL cubriendo:
 * <ul>
 * <li>Operaciones CRUD (Save, Update, Get, Delete).</li>
 * <li>Manejo exhaustivo de SQLException y su transformación a excepciones de dominio.</li>
 * <li>Gestión de flujos de error (usuario no encontrado después de insertar).</li>
 * <li>Cierre correcto de recursos de JDBC.</li>
 * </ul>
 * Clean Code - Regla 11: Documentación exhaustiva, estructura AAA y aserciones expresivas.
 */
@DisplayName("Pruebas Unitarias: UserRepositoryMySQL")
@ExtendWith(MockitoExtension.class)
class UserRepositoryMySQLTest {

  private static final String ID = "u-001";
  private static final String NAME = "John Doe";
  private static final String EMAIL = "john@example.com";
  private static final String HASH = "$2a$12$abcdefghijklmnopqrstuO";
  private static final String ROLE = "ADMIN";
  private static final String STATUS = "ACTIVE";
  private static final String CREATED_AT = "2024-01-01";
  private static final String UPDATED_AT = "2024-01-02";

  @Mock private Connection connection;
  @Mock private PreparedStatement statement;
  @Mock private ResultSet resultSet;

  private UserRepositoryMySQL repository;
  private UserModel userModel;
  private UserId userId;
  private UserEmail userEmail;

  @BeforeEach
  void setUp() {
    repository = new UserRepositoryMySQL(connection);
    userId = new UserId(ID);
    userEmail = new UserEmail(EMAIL);
    userModel = new UserModel(
            userId,
            new UserName(NAME),
            userEmail,
            UserPassword.fromHash(HASH),
            UserRole.ADMIN,
            UserStatus.ACTIVE);
  }

  // ── Métodos Helper para reducir ruido en Arrange

  private void configureStatementAndResultSet() throws SQLException {
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenReturn(resultSet);
  }

  private void configureResultSetRow() throws SQLException {
    when(resultSet.getString("id")).thenReturn(ID);
    when(resultSet.getString("name")).thenReturn(NAME);
    when(resultSet.getString("email")).thenReturn(EMAIL);
    when(resultSet.getString("password")).thenReturn(HASH);
    when(resultSet.getString("role")).thenReturn(ROLE);
    when(resultSet.getString("status")).thenReturn(STATUS);
    when(resultSet.getString("created_at")).thenReturn(CREATED_AT);
    when(resultSet.getString("updated_at")).thenReturn(UPDATED_AT);
  }

  // ── save()

  @Test
  @DisplayName("save() debe insertar el usuario y retornarlo consultando por ID")
  void shouldSaveUserAndReturnById() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final UserModel result = repository.save(userModel);

    // Assert
    assertAll("Persistencia de nuevo usuario",
            () -> assertEquals(ID, result.getId().value()),
            () -> assertEquals(NAME, result.getName().value()),
            () -> verify(statement).executeUpdate()
    );
  }

  @Test
  @DisplayName("save() debe lanzar PersistenceException si el INSERT falla en SQL")
  void shouldThrowPersistenceExceptionWhenInsertFails() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeUpdate()).thenThrow(new SQLException("Error en INSERT"));

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.save(userModel));
  }

  @Test
  @DisplayName("save() debe lanzar UserNotFoundException si el usuario no se encuentra tras el INSERT")
  void shouldThrowUserNotFoundExceptionWhenUserNotFoundAfterSave() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(false);

    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> repository.save(userModel));
  }

  // ── update()

  @Test
  @DisplayName("update() debe ejecutar el UPDATE y retornar el usuario actualizado")
  void shouldUpdateUserAndReturnById() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final UserModel result = repository.update(userModel);

    // Assert
    assertAll("Actualización de usuario",
            () -> assertEquals(ID, result.getId().value()),
            () -> verify(statement).executeUpdate()
    );
  }

  @Test
  @DisplayName("update() debe lanzar PersistenceException si el UPDATE falla en SQL")
  void shouldThrowPersistenceExceptionWhenUpdateFails() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeUpdate()).thenThrow(new SQLException("Error en UPDATE"));

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.update(userModel));
  }

  // ── getById()

  @Test
  @DisplayName("getById() debe retornar Optional con el usuario si el ID existe")
  void shouldReturnUserWhenFound() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final Optional<UserModel> result = repository.getById(userId);

    // Assert
    assertAll("Búsqueda por ID",
            () -> assertTrue(result.isPresent()),
            () -> assertEquals(ID, result.get().getId().value())
    );
  }

  @Test
  @DisplayName("getById() debe retornar Optional vacío si el ID no existe")
  void shouldReturnEmptyWhenNotFound() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(false);

    // Act
    final Optional<UserModel> result = repository.getById(userId);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("getById() debe lanzar PersistenceException ante fallos de conexión o consulta")
  void shouldThrowPersistenceExceptionOnGetByIdFailure() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Fallo en JDBC"));

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.getById(userId));
  }

  // ── getByEmail()

  @Test
  @DisplayName("getByEmail() debe retornar Optional con el usuario si el email existe")
  void shouldReturnUserByEmailWhenFound() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final Optional<UserModel> result = repository.getByEmail(userEmail);

    // Assert
    assertAll("Búsqueda por Email",
            () -> assertTrue(result.isPresent()),
            () -> assertEquals(EMAIL, result.get().getEmail().value())
    );
  }

  @Test
  @DisplayName("getByEmail() debe lanzar PersistenceException si falla el cierre de recursos")
  void shouldThrowPersistenceExceptionWhenGetByEmailStatementCloseFails() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(false);
    doThrow(new SQLException("Error al cerrar Statement")).when(statement).close();

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.getByEmail(userEmail));
  }

  // ── getAll()

  @Test
  @DisplayName("getAll() debe retornar la lista completa de usuarios")
  void shouldReturnAllUsers() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true, false);
    configureResultSetRow();

    // Act
    final List<UserModel> result = repository.getAll();

    // Assert
    assertAll("Consulta de todos los usuarios",
            () -> assertEquals(1, result.size()),
            () -> assertEquals(ID, result.get(0).getId().value())
    );
  }

  @Test
  @DisplayName("getAll() debe lanzar PersistenceException ante fallos en el SELECT")
  void shouldThrowPersistenceExceptionOnGetAllFailure() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Error en consulta masiva"));

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.getAll());
  }

  // ── delete()

  @Test
  @DisplayName("delete() debe ejecutar el DELETE correctamente sin lanzar excepciones")
  void shouldDeleteUserWithoutThrowing() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenReturn(statement);

    // Act & Assert
    assertDoesNotThrow(() -> repository.delete(userId));
    verify(statement).executeUpdate();
  }

  @Test
  @DisplayName("delete() debe lanzar PersistenceException si falla la eliminación en base de datos")
  void shouldThrowPersistenceExceptionWhenDeleteFails() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Fallo en DELETE"));

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.delete(userId));
  }
}