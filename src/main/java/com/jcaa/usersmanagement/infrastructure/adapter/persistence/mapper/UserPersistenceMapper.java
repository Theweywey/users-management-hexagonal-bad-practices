package com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper para transformar datos de usuario entre las capas de persistencia y dominio.
 * Cumple con la Regla 4: Uso de instancia en lugar de métodos estáticos.
 */
public class UserPersistenceMapper {

  public UserPersistenceMapper() {
    // Constructor público para permitir la instanciación
  }

  public UserPersistenceDto fromModelToDto(UserModel user) {
    return new UserPersistenceDto(
            user.getId().value(),
            user.getName().value(),
            user.getEmail().value(),
            user.getPassword().value(),
            user.getRole().name(),
            user.getStatus().name(),
            null,
            null);
  }

  public UserModel fromEntityToModel(UserEntity entity) {
    return new UserModel(
            new UserId(entity.id()),
            new UserName(entity.name()),
            new UserEmail(entity.email()),
            UserPassword.fromHash(entity.password()),
            UserRole.valueOf(entity.role()),
            UserStatus.valueOf(entity.status()));
  }

  public UserEntity fromResultSetToEntity(ResultSet rs) throws SQLException {
    return new UserEntity(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("role"),
            rs.getString("status"),
            rs.getString("created_at"),
            rs.getString("updated_at"));
  }

  public List<UserModel> fromResultSetToModelList(ResultSet rs) throws SQLException {
    final List<UserModel> users = new ArrayList<>();
    while (rs.next()) {
      users.add(fromEntityToModel(fromResultSetToEntity(rs)));
    }
    return users;
  }
}