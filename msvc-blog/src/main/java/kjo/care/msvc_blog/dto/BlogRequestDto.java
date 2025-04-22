package kjo.care.msvc_blog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogRequestDto {

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    @NotNull(message = "El título no puede estar vacío")
    @Size(max = 255, message = "El título debe tener máximo 255 caracteres")
    private String title;

    @NotNull(message = "El contenido no puede estar vacío")
    private String content;

    private String video;
    private String image;
}
