package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetAllUsersPort;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para GetAllUsersService.
 * * <p>Esta suite verifica:
 * <ul>
 * <li>Recuperación exitosa de la lista completa de usuarios desde el puerto.</li>
 * <li>Manejo correcto de casos sin datos (retorno de lista vacía en lugar de null).</li>
 * </ul>
 * * Clean Code - Regla 11: Documentación, estructura AAA y aserciones expresivas.
 */
@DisplayName("Pruebas Unitarias: GetAllUsersService")
@ExtendWith(MockitoExtension.class)
class GetAllUsersServiceTest {

  @Mock private GetAllUsersPort getAllUsersPort;

  private GetAllUsersService service;

  @BeforeEach
  void setUp() {
    service = new GetAllUsersService(getAllUsersPort);
  }

  @Test
  @DisplayName("Debe retornar la lista de usuarios obtenida desde el puerto")
  void shouldReturnUsersFromPort() {
    // Arrange
    final UserModel user =
            new UserModel(
                    new UserId("u-001"),
                    new UserName("John Arrieta"),
                    new UserEmail("john@example.com"),
                    UserPassword.fromHash("$2a$12$abcdefghijklmnopqrstuO"),
                    UserRole.ADMIN,
                    UserStatus.ACTIVE);
    when(getAllUsersPort.getAll()).thenReturn(List.of(user));

    // Act
    final List<UserModel> result = service.execute();

    // Assert
    // Regla 11: Aserciones expresivas que informan cantidad y referencia exacta
    assertEquals(1, result.size(), "La lista debe contener exactamente un usuario");
    assertSame(user, result.get(0), "El usuario en la lista debe ser la misma instancia del puerto");
  }

  @Test
  @DisplayName("Debe retornar una lista vacía cuando no existen usuarios")
  void shouldReturnEmptyListWhenNoUsers() {
    // Arrange
    when(getAllUsersPort.getAll()).thenReturn(List.of());

    // Act
    final List<UserModel> result = service.execute();

    // Assert
    // Regla 11: Se corrige el bug. El resultado debe ser una lista vacía, no null.
    assertNotNull(result, "El resultado nunca debe ser null");
    assertTrue(result.isEmpty(), "La lista debe estar vacía si no hay usuarios");
  }
}