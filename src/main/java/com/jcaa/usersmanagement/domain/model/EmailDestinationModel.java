package com.jcaa.usersmanagement.domain.model;

import java.util.Objects;
import lombok.Value;

@Value
public class EmailDestinationModel {

  // Regla 10: Centralización de mensajes de error en constantes
  private static final String EMAIL_REQUIRED_MSG = "El email del destinatario es requerido.";
  private static final String NAME_REQUIRED_MSG = "El nombre del destinatario es requerido.";
  private static final String SUBJECT_REQUIRED_MSG = "El asunto es requerido.";
  private static final String BODY_REQUIRED_MSG = "El cuerpo del mensaje es requerido.";

  String destinationEmail;
  String destinationName;
  String subject;
  String body;

  public EmailDestinationModel(
          final String destinationEmail,
          final String destinationName,
          final String subject,
          final String body) {
    this.destinationEmail = validateNotBlank(destinationEmail, EMAIL_REQUIRED_MSG);
    this.destinationName = validateNotBlank(destinationName, NAME_REQUIRED_MSG);
    this.subject = validateNotBlank(subject, SUBJECT_REQUIRED_MSG);
    this.body = validateNotBlank(body, BODY_REQUIRED_MSG);
  }

  private static String validateNotBlank(final String value, final String errorMessage) {
    // Regla 4: Uso de utilidades estándar de Java para validación de nulos
    Objects.requireNonNull(value, errorMessage);

    if (value.trim().isEmpty()) {
      throw new IllegalArgumentException(errorMessage);
    }
    return value;
  }
}