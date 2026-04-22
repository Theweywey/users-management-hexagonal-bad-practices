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
import lombok.experimental.UtilityClass;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clean Code - Regla 4: Uso de @UtilityClass para mappers manuales.
 * Clean Code - Regla 13: Aunque MapStruct es el estándar, este mapper manual
 * se organiza como una utilidad pura para evitar instanciación innecesaria.
 */
@UtilityClass
public class UserPersistenceMapper {

  public static UserPersistenceDto fromModelToDto(final UserModel user) {
    // Regla 14 (Ley de Deméter): Se accede a los datos del modelo.
    // Nota: En un refactor profundo, el UserModel debería proveer estos strings directamente.
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

  public static UserEntity fromResultSetToEntity(final ResultSet resultSet) throws SQLException {
    return new UserEntity(
            resultSet.getString("id"),
            resultSet.getString("name"),
            resultSet.getString("email"),
            resultSet.getString("password"),
            resultSet.getString("role"),
            resultSet.getString("status"),
            resultSet.getString("created_at"),
            resultSet.getString("updated_at"));
  }

  public static UserModel fromEntityToModel(final UserEntity entity) {
    return new UserModel(
            new UserId(entity.id()),
            new UserName(entity.name()),
            new UserEmail(entity.email()),
            UserPassword.fromHash(entity.password()),
            UserRole.fromString(entity.role()),
            UserStatus.fromString(entity.status()));
  }

  public static UserModel fromResultSetToModel(final ResultSet resultSet) throws SQLException {
    return fromEntityToModel(fromResultSetToEntity(resultSet));
  }

  public static List<UserModel> fromResultSetToModelList(final ResultSet resultSet) throws SQLException {
    final List<UserModel> users = new ArrayList<>();
    while (resultSet.next()) {
      users.add(fromResultSetToModel(resultSet));
    }
    return users;
  }
}