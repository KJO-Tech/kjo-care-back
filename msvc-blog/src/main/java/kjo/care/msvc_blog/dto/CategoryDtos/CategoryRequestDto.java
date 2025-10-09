package kjo.care.msvc_blog.dto.CategoryDtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "CategoryRequestDto")
public class CategoryRequestDto {

    @NotNull (message = "Nomnre requerido")
    private String name;
}
