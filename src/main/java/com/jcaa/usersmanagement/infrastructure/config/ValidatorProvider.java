package com.jcaa.usersmanagement.infrastructure.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.experimental.UtilityClass;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

/**
 * Proveedor de validaciones para la capa de aplicación.
 * * Clean Code - Regla 4: Uso de @UtilityClass.
 * Al ser una clase que solo ofrece una herramienta estática, no debe ser instanciable.
 * Lombok se encarga de hacer la clase final y crear el constructor privado.
 */
@UtilityClass
public final class ValidatorProvider {

  public static Validator buildValidator() {
    try (final ValidatorFactory factory = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()) {
      return factory.getValidator();
    }
  }
}