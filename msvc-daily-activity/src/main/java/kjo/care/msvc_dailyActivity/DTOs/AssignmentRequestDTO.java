package kjo.care.msvc_dailyActivity.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentRequestDTO {

    @NotNull(message = "El ID del ejercicio es obligatorio")
    private UUID exerciseId;
}