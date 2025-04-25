package kjo.care.msvc_blog.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDto {

    @NotNull(message = "El blog no puede estar vacío")
    private Long blogId;

    @NotNull(message = "El contenido no puede estar vacío")
    private String content;

    @Nullable
    private Long commentParentId;
}
