package com.jcaa.usersmanagement.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para EmailDestinationModel.
 * <p>Verifica la integridad de los datos de envío de correo, asegurando que:
 * <ul>
 * <li>Los objetos se construyan correctamente con datos válidos.</li>
 * <li>Cada campo (email, nombre, asunto y cuerpo) sea validado contra nulos o vacíos.</li>
 * </ul>
 * Clean Code - Regla 11: Estructura AAA y validación exhaustiva de contratos.
 * Clean Code - Regla 10: Uso de constantes para centralizar datos de prueba.
 */
@DisplayName("Pruebas Unitarias: EmailDestinationModel")
class EmailDestinationModelTest {

  // Regla 10: Datos de prueba centralizados
  private static final String EMAIL = "dest@example.com";
  private static final String NAME = "Recipient Name";
  private static final String SUBJECT = "Welcome";
  private static final String BODY = "Hello, welcome to the platform.";

  @Test
  @DisplayName("Debe preservar todos los campos cuando se construye con datos válidos")
  void shouldCreateModelWithAllValidFields() {
    // Arrange & Act
    final EmailDestinationModel model = new EmailDestinationModel(EMAIL, NAME, SUBJECT, BODY);

    // Assert
    assertAll(
            "Verificación de integridad de los campos del modelo",
            () -> assertEquals(EMAIL, model.getDestinationEmail(), "El email de destino no coincide"),
            () -> assertEquals(NAME, model.getDestinationName(), "El nombre de destino no coincide"),
            () -> assertEquals(SUBJECT, model.getSubject(), "El asunto no coincide"),
            () -> assertEquals(BODY, model.getBody(), "El cuerpo del mensaje no coincide")
    );
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando el email de destino es nulo")
  void shouldThrowNpeWhenDestinationEmailIsNull() {
    // Act & Assert
    // Regla 11: Se documenta que el fallo ocurre en el primer punto de validación
    assertThrows(
            NullPointerException.class,
            () -> new EmailDestinationModel(null, NAME, SUBJECT, BODY),
            "Se esperaba NPE al pasar email nulo"
    );
  }

  @Test
  @DisplayName("Debe lanzar IllegalArgumentException cuando el nombre de destino está en blanco")
  void shouldThrowIaeWhenDestinationNameIsBlank() {
    // Act & Assert
    // Regla 11: Se documenta que el fallo ocurre tras validar el email
    assertThrows(
            IllegalArgumentException.class,
            () -> new EmailDestinationModel(EMAIL, "   ", SUBJECT, BODY),
            "Se esperaba IAE al pasar nombre en blanco"
    );
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando el asunto es nulo")
  void shouldThrowNpeWhenSubjectIsNull() {
    // Act & Assert
    assertThrows(
            NullPointerException.class,
            () -> new EmailDestinationModel(EMAIL, NAME, null, BODY),
            "Se esperaba NPE al pasar asunto nulo"
    );
  }

  @Test
  @DisplayName("Debe lanzar IllegalArgumentException cuando el cuerpo del mensaje está vacío")
  void shouldThrowIaeWhenBodyIsEmpty() {
    // Act & Assert
    assertThrows(
            IllegalArgumentException.class,
            () -> new EmailDestinationModel(EMAIL, NAME, SUBJECT, ""),
            "Se esperaba IAE al pasar cuerpo vacío"
    );
  }
}