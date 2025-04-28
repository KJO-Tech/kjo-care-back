package kjo.care.msvc_blog.dto;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogRequestDto {

    @Schema(description = "ID de la categoría", defaultValue = "0")
    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    @NotNull(message = "El título no puede estar vacío")
    @Size(max = 255, message = "El título debe tener máximo 255 caracteres")
    private String title;

    @NotNull(message = "El contenido no puede estar vacío")
    private String content;

    @Schema(description = "Archivo de video (opcional)", format = "binary", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            extensions = {@Extension( name = "x-spring-content",  properties = @ExtensionProperty(name = "handler", value = "ignoreEmptyFile"))}
    )
    @Nullable
    private MultipartFile video;

    @Schema(description = "Archivo de imagen (opcional)", format = "binary", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            extensions = {@Extension( name = "x-spring-content",  properties = @ExtensionProperty(name = "handler", value = "ignoreEmptyFile"))}
    )
    @Nullable
    private MultipartFile image;
}
