package kjo.care.msvc_moodTracking.DTOs.MoodDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estado de animo del usuario ")
public class MoodRequestDto {
    @NotNull(message = "El nombre es requerido")
    private String name;
    @NotNull(message = "La descripcion es requerida")
    private String description;
    @NotNull(message = "El estado es requerido")
    private String state;
    @NotNull(message = "La imagen es requerida")
    private String image;
    @NotNull(message = "El color es requerido")
    private String color;
}
