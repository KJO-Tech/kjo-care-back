package kjo.care.msvc_dailyActivity.DTOs;

import jakarta.validation.constraints.*;
import kjo.care.msvc_dailyActivity.Enums.ExerciseContentType;
import kjo.care.msvc_dailyActivity.Enums.ExerciseDifficultyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyExerciseRequestDTO {

    @NotNull(message = "El ID de la categoría es obligatorio")
    private UUID categoryId;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 150, message = "El título debe tener entre 3 y 150 caracteres")
    private String title;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;

    @NotNull(message = "La duración en minutos es obligatoria")
    @Positive(message = "La duración debe ser positiva")
    @Max(value = 300, message = "La duración no puede exceder 300 minutos")
    private Integer durationMinutes;

    @NotNull(message = "El tipo de contenido es obligatorio")
    private ExerciseContentType contentType;

    @Size(max = 500, message = "La URL del contenido no puede exceder 500 caracteres")
    private String contentUrl;

    @Size(max = 500, message = "La URL del thumbnail no puede exceder 500 caracteres")
    private String thumbnailUrl;

    @NotNull(message = "La dificultad es obligatoria")
    private ExerciseDifficultyType difficulty;
}