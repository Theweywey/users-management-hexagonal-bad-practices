package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetUserByIdPort;
import com.jcaa.usersmanagement.application.service.dto.query.GetUserByIdQuery;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.exception.UserNotFoundException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para GetUserByIdService.
 * * <p>Cubre los escenarios de:
 * <ul>
 * <li>Búsqueda exitosa con retorno de modelo.</li>
 * <li>Manejo de excepción cuando el usuario no existe.</li>
 * <li>Validación de integridad del Query de entrada.</li>
 * </ul>
 * * Clean Code - Regla 11: Documentación de casos, estructura AAA y aserciones expresivas.
 */
@DisplayName("Pruebas Unitarias: GetUserByIdService")
@ExtendWith(MockitoExtension.class)
class GetUserByIdServiceTest {

  private static final String VALID_ID = "u-001";
  private static final String INVALID_ID = "no-existe";

  @Mock private GetUserByIdPort getUserByIdPort;

  private GetUserByIdService service;

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service = new GetUserByIdService(getUserByIdPort, validatorFactory.getValidator());
    }
  }

  @Test
  @DisplayName("Debe retornar el usuario cuando el ID existe en el sistema")
  void shouldReturnUserWhenFound() {
    // Arrange
    final GetUserByIdQuery query = new GetUserByIdQuery(VALID_ID);
    final UserModel expected = new UserModel(
            new UserId(VALID_ID),
            new UserName("John Arrieta"),
            new UserEmail("john@example.com"),
            UserPassword.fromHash("$2a$12$abcdefghijklmnopqrstuO"),
            UserRole.ADMIN,
            UserStatus.ACTIVE);

    when(getUserByIdPort.getById(any())).thenReturn(Optional.of(expected));

    // Act
    final UserModel result = service.execute(query);

    // Assert
    // Regla 11: assertSame verifica identidad de objeto, assertNotNull verifica existencia.
    assertNotNull(result, "El usuario retornado no debe ser nulo");
    assertSame(expected, result, "El objeto retornado debe ser la misma instancia que provee el puerto");
  }

  @Test
  @DisplayName("Debe lanzar UserNotFoundException cuando el ID no existe")
  void shouldThrowWhenUserNotFound() {
    // Arrange
    final GetUserByIdQuery query = new GetUserByIdQuery(INVALID_ID);
    when(getUserByIdPort.getById(any())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> service.execute(query),
            "Se esperaba UserNotFoundException para un ID inexistente");
  }

  @Test
  @DisplayName("Debe lanzar ConstraintViolationException cuando el Query tiene un ID vacío")
  void shouldThrowWhenQueryIsInvalid() {
    // Arrange
    final GetUserByIdQuery query = new GetUserByIdQuery("");

    // Act & Assert
    assertThrows(ConstraintViolationException.class, () -> service.execute(query));
    verifyNoInteractions(getUserByIdPort);
  }
}