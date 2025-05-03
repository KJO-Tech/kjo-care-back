package kjo.care.msvc_emergency.dto;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyRequestDto {

    @NotNull(message = "El nombre no puede estar vacío")
    private String name;

    @NotNull(message = "El descripción no puede estar vacío")
    private String description;

    @Schema(description = "Archivo de imagen (opcional)", format = "binary", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            extensions = {@Extension(name = "x-spring-content", properties = @ExtensionProperty(name = "handler", value = "ignoreEmptyFile"))}
    )
    @Nullable
    private MultipartFile imageUrl;

    @Schema(description = "Archivo de video (opcional)", format = "binary", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            extensions = {@Extension(name = "x-spring-content", properties = @ExtensionProperty(name = "handler", value = "ignoreEmptyFile"))}
    )
    @Nullable
    private MultipartFile videoUrl;

    private List<String> contacts;

    private List<String> links;
}
