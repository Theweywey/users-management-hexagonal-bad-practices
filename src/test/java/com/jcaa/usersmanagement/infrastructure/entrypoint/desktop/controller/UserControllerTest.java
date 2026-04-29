package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.in.*;
import com.jcaa.usersmanagement.application.service.dto.command.*;
import com.jcaa.usersmanagement.application.service.dto.query.GetUserByIdQuery;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.exception.InvalidCredentialsException;
import com.jcaa.usersmanagement.domain.exception.UserAlreadyExistsException;
import com.jcaa.usersmanagement.domain.exception.UserNotFoundException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.CreateUserRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.LoginRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UpdateUserRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UserResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para UserController.
 * <p>Valida el flujo de entrada de la aplicación desktop:
 * <ul>
 * <li>Mapeo correcto de Requests a Comandos/Queries.</li>
 * <li>Delegación a los puertos de los casos de uso (Ports In).</li>
 * <li>Transformación de Modelos de Dominio a Responses (DTOs).</li>
 * <li>Propagación transparente de excepciones de negocio.</li>
 * </ul>
 * Clean Code - Regla 11: Estructura AAA y documentación de flujos.
 * Clean Code - Regla 10: Uso de constantes para centralizar datos de prueba.
 */
@DisplayName("Pruebas Unitarias: UserController")
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  // Regla 10: Datos de prueba centralizados
  private static final String TEST_ID = "u-001";
  private static final String TEST_NAME = "Alice Smith";
  private static final String TEST_EMAIL = "alice@example.com";
  private static final String TEST_PASS = "Pass1234!";
  private static final String BCRYPT_HASH = "$2a$12$abcdefghijklmnopqrstuvwxyz1234567890";

  @Mock private CreateUserUseCase createUserUseCase;
  @Mock private UpdateUserUseCase updateUserUseCase;
  @Mock private DeleteUserUseCase deleteUserUseCase;
  @Mock private GetUserByIdUseCase getUserByIdUseCase;
  @Mock private GetAllUsersUseCase getAllUsersUseCase;
  @Mock private LoginUseCase loginUseCase;

  private UserController controller;

  @BeforeEach
  void setUp() {
    controller = new UserController(
            createUserUseCase,
            updateUserUseCase,
            deleteUserUseCase,
            getUserByIdUseCase,
            getAllUsersUseCase,
            loginUseCase);
  }

  // ── listAllUsers

  @Test
  @DisplayName("Debe retornar lista de UserResponse cuando existen usuarios en el sistema")
  void listAllUsers_ShouldReturnMappedResponseList() {
    // Arrange
    final UserModel user = buildUser(TEST_ID, TEST_NAME, TEST_EMAIL, UserRole.ADMIN, UserStatus.ACTIVE);
    when(getAllUsersUseCase.execute()).thenReturn(List.of(user));

    // Act
    final List<UserResponse> result = controller.listAllUsers();

    // Assert
    assertAll("Mapeo de lista de usuarios",
            () -> assertEquals(1, result.size()),
            () -> assertEquals(TEST_ID, result.get(0).getId()),
            () -> assertEquals(TEST_NAME, result.get(0).getName())
    );
    verify(getAllUsersUseCase).execute();
  }

  // ── findUserById

  @Test
  @DisplayName("Debe retornar el usuario mapeado cuando el ID existe")
  void findUserById_ShouldReturnResponse_WhenUserExists() {
    // Arrange
    final UserModel user = buildUser(TEST_ID, TEST_NAME, TEST_EMAIL, UserRole.MEMBER, UserStatus.ACTIVE);
    when(getUserByIdUseCase.execute(any())).thenReturn(user);

    // Act
    final UserResponse result = controller.findUserById(TEST_ID);

    // Assert
    assertAll("Mapeo de respuesta individual",
            () -> assertEquals(TEST_ID, result.getId()),
            () -> assertEquals(TEST_EMAIL, result.getEmail())
    );
  }

  @Test
  @DisplayName("Debe propagar UserNotFoundException cuando el ID no existe")
  void findUserById_ShouldPropagateException_WhenUserDoesNotExist() {
    // Arrange
    when(getUserByIdUseCase.execute(any())).thenThrow(UserNotFoundException.becauseIdWasNotFound(TEST_ID));

    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> controller.findUserById(TEST_ID));
  }

  // ── createUser

  @Test
  @DisplayName("Debe delegar la creación al caso de uso y retornar el usuario creado")
  void createUser_ShouldDelegateAndReturnResponse() {
    // Arrange
    final CreateUserRequest request = new CreateUserRequest(TEST_ID, TEST_NAME, TEST_EMAIL, TEST_PASS, "REVIEWER");
    final UserModel createdUser = buildUser(TEST_ID, TEST_NAME, TEST_EMAIL, UserRole.REVIEWER, UserStatus.PENDING);

    final ArgumentCaptor<CreateUserCommand> captor = ArgumentCaptor.forClass(CreateUserCommand.class);
    when(createUserUseCase.execute(captor.capture())).thenReturn(createdUser);

    // Act
    final UserResponse result = controller.createUser(request);

    // Assert
    assertAll("Delegación de comando de creación",
            () -> assertEquals(TEST_ID, captor.getValue().id()),
            () -> assertEquals("PENDING", result.getStatus()),
            () -> verify(createUserUseCase).execute(any())
    );
  }

  // ── login

  @Test
  @DisplayName("Debe retornar el usuario logueado cuando las credenciales son válidas")
  void login_ShouldReturnResponse_WhenCredentialsAreValid() {
    // Arrange
    final LoginRequest request = new LoginRequest(TEST_EMAIL, TEST_PASS);
    final UserModel loggedUser = buildUser(TEST_ID, TEST_NAME, TEST_EMAIL, UserRole.MEMBER, UserStatus.ACTIVE);

    final ArgumentCaptor<LoginCommand> captor = ArgumentCaptor.forClass(LoginCommand.class);
    when(loginUseCase.execute(captor.capture())).thenReturn(loggedUser);

    // Act
    final UserResponse result = controller.login(request);

    // Assert
    assertAll("Mapeo de login",
            () -> assertEquals(TEST_EMAIL, captor.getValue().email()),
            () -> assertEquals(TEST_ID, result.getId()),
            () -> assertEquals("ACTIVE", result.getStatus())
    );
  }

  @Test
  @DisplayName("Debe propagar InvalidCredentialsException cuando falla el login")
  void login_ShouldPropagateException_WhenCredentialsAreInvalid() {
    // Arrange
    final LoginRequest request = new LoginRequest(TEST_EMAIL, "WrongPass");
    when(loginUseCase.execute(any())).thenThrow(InvalidCredentialsException.becauseCredentialsAreInvalid());

    // Act & Assert
    assertThrows(InvalidCredentialsException.class, () -> controller.login(request));
  }

  // ── Helper

  private UserModel buildUser(String id, String name, String email, UserRole role, UserStatus status) {
    return new UserModel(
            new UserId(id),
            new UserName(name),
            new UserEmail(email),
            UserPassword.fromHash(BCRYPT_HASH),
            role,
            status);
  }
}