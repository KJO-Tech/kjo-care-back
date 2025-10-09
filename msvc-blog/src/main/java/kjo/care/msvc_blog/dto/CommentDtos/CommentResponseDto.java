package kjo.care.msvc_blog.dto.CommentDtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import kjo.care.msvc_blog.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private UUID id;
    private UUID blogId;
    private UserInfoDto userId;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate commentDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate modifiedDate;

    private Long commentParentId;
}
