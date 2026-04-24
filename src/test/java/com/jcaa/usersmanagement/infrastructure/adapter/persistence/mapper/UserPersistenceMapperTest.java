package com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.dto.UserPersistenceDto;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.entity.UserEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para UserPersistenceMapper.
 * Verifica el mapeo bidireccional entre dominio, DTOs y entidades de base de datos.
 * Clean Code - Regla 11: Documentación y estructura AAA.
 */
@DisplayName("Pruebas Unitarias: UserPersistenceMapper")
@ExtendWith(MockitoExtension.class)
class UserPersistenceMapperTest {

  private static final String ID = "u-001";
  private static final String NAME = "John Doe";
  private static final String EMAIL = "john@example.com";
  private static final String HASH = "$2a$12$abcdefghijklmnopqrstuO";
  private static final String ROLE = "ADMIN";
  private static final String STATUS = "ACTIVE";
  private static final String CREATED_AT = "2024-01-01 00:00:00";
  private static final String UPDATED_AT = "2024-01-02 00:00:00";

  @Mock private ResultSet resultSet;

  private UserPersistenceMapper mapper;
  private UserModel userModel;
  private UserEntity userEntity;

  @BeforeEach
  void setUp() {
    // Arrange Inicial
    mapper = new UserPersistenceMapper();

    userModel = new UserModel(
            new UserId(ID),
            new UserName(NAME),
            new UserEmail(EMAIL),
            UserPassword.fromHash(HASH),
            UserRole.ADMIN,
            UserStatus.ACTIVE);

    userEntity = new UserEntity(ID, NAME, EMAIL, HASH, ROLE, STATUS, CREATED_AT, UPDATED_AT);
  }

  @Test
  @DisplayName("Debe mapear UserModel a UserPersistenceDto correctamente")
  void shouldMapModelToDto() {
    // Act
    final UserPersistenceDto result = mapper.fromModelToDto(userModel);

    // Assert
    assertAll("Mapeo a DTO",
            () -> assertEquals(ID, result.id()),
            () -> assertEquals(NAME, result.name()),
            () -> assertNull(result.createdAt())
    );
  }

  @Test
  @DisplayName("Debe transformar UserEntity a UserModel correctamente")
  void shouldMapEntityToModel() {
    // Act
    final UserModel result = mapper.fromEntityToModel(userEntity);

    // Assert
    assertAll("Mapeo a Modelo",
            () -> assertEquals(ID, result.getId().value()),
            () -> assertEquals(UserRole.ADMIN, result.getRole())
    );
  }

  @Test
  @DisplayName("Debe leer el ResultSet y generar una UserEntity")
  void shouldReadAllColumnsFromResultSet() throws SQLException {
    // Arrange
    setupResultSetMock();

    // Act
    final UserEntity result = mapper.fromResultSetToEntity(resultSet);

    // Assert
    assertEquals(ID, result.id());
  }

  @Test
  @DisplayName("Debe retornar una lista vacía si el ResultSet no tiene filas")
  void shouldReturnEmptyListWhenResultSetIsEmpty() throws SQLException {
    // Arrange
    when(resultSet.next()).thenReturn(false);

    // Act
    final List<UserModel> result = mapper.fromResultSetToModelList(resultSet);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Debe propagar SQLException ante errores de lectura")
  void shouldPropagateException() throws SQLException {
    // Arrange
    when(resultSet.getString(anyString())).thenThrow(new SQLException("DB Error"));

    // Act & Assert
    assertThrows(SQLException.class, () -> mapper.fromResultSetToEntity(resultSet));
  }

  private void setupResultSetMock() throws SQLException {
    when(resultSet.getString("id")).thenReturn(ID);
    when(resultSet.getString("name")).thenReturn(NAME);
    when(resultSet.getString("email")).thenReturn(EMAIL);
    when(resultSet.getString("password")).thenReturn(HASH);
    when(resultSet.getString("role")).thenReturn(ROLE);
    when(resultSet.getString("status")).thenReturn(STATUS);
    when(resultSet.getString("created_at")).thenReturn(CREATED_AT);
    when(resultSet.getString("updated_at")).thenReturn(UPDATED_AT);
  }
}