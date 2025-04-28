package kjo.care.msvc_blog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionRequestDto {
    @NotNull(message = "El blog no puede estar vacío")
    private Long blogId;
}
