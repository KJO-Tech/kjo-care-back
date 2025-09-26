package kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MoodUserRequestDto(
        @NotNull(message = "El ID del estado de animo es requerido")
        UUID moodId
) {
}
