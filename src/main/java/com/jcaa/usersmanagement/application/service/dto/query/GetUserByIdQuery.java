package com.jcaa.usersmanagement.application.service.dto.query;

import jakarta.validation.constraints.NotBlank;

/**
 * Clean Code - Regla 3: Simplicidad en los DTOs.
 * Se elimina @Builder ya que los Records proporcionan un constructor canónico compacto.
 * Se eliminan los mensajes personalizados de las constraints para usar los valores por defecto.
 */
public record GetUserByIdQuery(
        @NotBlank String id) {
}