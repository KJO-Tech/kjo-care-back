package kjo.care.msvc_dailyActivity.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteExerciseRequestDTO {

    @NotNull(message = "El estado de completado es obligatorio")
    private Boolean completed;
}