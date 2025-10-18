package kjo.care.msvc_blog.dto.CommentDtos;

import jakarta.annotation.Nullable;
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
public class CommentRequestDto {

    @NotNull(message = "El blog no puede estar vacío")
    private UUID blogId;

    @NotNull(message = "El contenido no puede estar vacío")
    private String content;

    @Nullable
    private UUID commentParentId;
}
