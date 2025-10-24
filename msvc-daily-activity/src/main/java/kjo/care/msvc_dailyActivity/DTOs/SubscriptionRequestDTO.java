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
public class SubscriptionRequestDTO {

    @NotNull(message = "El ID de la categoría es obligatorio")
    private UUID categoryId;
}