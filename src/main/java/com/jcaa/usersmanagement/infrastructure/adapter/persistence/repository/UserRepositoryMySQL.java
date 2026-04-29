package com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository;

import com.jcaa.usersmanagement.application.port.out.DeleteUserPort;
import com.jcaa.usersmanagement.application.port.out.GetAllUsersPort;
import com.jcaa.usersmanagement.application.port.out.GetUserByEmailPort;
import com.jcaa.usersmanagement.application.port.out.GetUserByIdPort;
import com.jcaa.usersmanagement.application.port.out.SaveUserPort;
import com.jcaa.usersmanagement.application.port.out.UpdateUserPort;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.PersistenceException;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper.UserPersistenceMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserRepositoryMySQL implements SaveUserPort, UpdateUserPort, DeleteUserPort, GetUserByIdPort, GetUserByEmailPort, GetAllUsersPort {

  private final Connection connection;
  private final UserPersistenceMapper mapper;

  public UserRepositoryMySQL(Connection connection) {
    this.connection = connection;
    this.mapper = new UserPersistenceMapper();
  }

  @Override
  public UserModel save(UserModel user) {
    var dto = this.mapper.fromModelToDto(user);
    String sql = "INSERT INTO users (id, name, email, password, role, status) VALUES (?, ?, ?, ?, ?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, dto.id());
      stmt.setString(2, dto.name());
      stmt.setString(3, dto.email());
      stmt.setString(4, dto.password());
      stmt.setString(5, dto.role());
      stmt.setString(6, dto.status());
      stmt.executeUpdate();

      return findByIdOrFail(user.getId());
    } catch (SQLException e) {
      throw new PersistenceException("Error al guardar usuario", e);
    }
  }

  @Override
  public UserModel update(UserModel user) {
    var dto = this.mapper.fromModelToDto(user);
    String sql = "UPDATE users SET name = ?, email = ?, password = ?, role = ?, status = ? WHERE id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, dto.name());
      stmt.setString(2, dto.email());
      stmt.setString(3, dto.password());
      stmt.setString(4, dto.role());
      stmt.setString(5, dto.status());
      stmt.setString(6, dto.id());
      stmt.executeUpdate();

      return findByIdOrFail(user.getId());
    } catch (SQLException e) {
      throw new PersistenceException("Error al actualizar usuario", e);
    }
  }

  @Override
  public void delete(UserId id) {
    String sql = "DELETE FROM users WHERE id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, id.value());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new PersistenceException("Error al eliminar usuario", e);
    }
  }

  @Override
  public Optional<UserModel> getById(UserId id) {
    String sql = "SELECT * FROM users WHERE id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, id.value());
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(this.mapper.fromEntityToModel(this.mapper.fromResultSetToEntity(rs)));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new PersistenceException("Error al buscar usuario por ID", e);
    }
  }

  @Override
  public Optional<UserModel> getByEmail(UserEmail email) {
    String sql = "SELECT * FROM users WHERE email = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, email.value());
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(this.mapper.fromEntityToModel(this.mapper.fromResultSetToEntity(rs)));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new PersistenceException("Error al buscar usuario por Email", e);
    }
  }

  @Override
  public List<UserModel> getAll() {
    String sql = "SELECT * FROM users";

    try (PreparedStatement stmt = connection.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
      return this.mapper.fromResultSetToModelList(rs);
    } catch (SQLException e) {
      throw new PersistenceException("Error al listar usuarios", e);
    }
  }

  private UserModel findByIdOrFail(UserId id) {
    return getById(id).orElseThrow(() -> new PersistenceException("Usuario no encontrado tras operación"));
  }
}