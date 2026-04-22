package com.jcaa.usersmanagement.domain.exception;

public final class EmailSenderException extends DomainException {

  // Regla 10: Se extraen los textos hardcodeados a constantes privadas
  private static final String SMTP_FAILURE_MESSAGE = "No se pudo enviar el correo a '%s'. Error SMTP: %s";
  private static final String GENERIC_FAILURE_MESSAGE = "La notificación por correo no pudo ser enviada.";

  // Regla 9: Constructores privados para forzar el uso de Factory Methods
  private EmailSenderException(final String message) {
    super(message);
  }

  private EmailSenderException(final String message, final Throwable cause) {
    super(message, cause);
  }

  // Factory Method: Controla exactamente cómo se crea esta excepción
  public static EmailSenderException becauseSmtpFailed(
          final String destinationEmail, final String smtpError) {
    return new EmailSenderException(
            String.format(SMTP_FAILURE_MESSAGE, destinationEmail, smtpError));
  }

  // Factory Method: Proporciona un nombre semántico al error
  public static EmailSenderException becauseSendFailed(final Throwable cause) {
    return new EmailSenderException(GENERIC_FAILURE_MESSAGE, cause);
  }
}