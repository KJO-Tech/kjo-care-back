package kjo.care.msvc_emergency.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthRequestDto {
    @NotNull(message = "El nombre no puede estar vacío")
    private String name;

    @NotNull(message = "La dirección no puede estar vacío")
    private String address;

    @NotNull(message = "El telefono no puede estar vacío")
    private String phone;

    @NotNull(message = "La latitud no puede estar vacía")
    private Double latitude;

    @NotNull(message = "La longitud no puede estar vacía")
    private Double longitude;

}
