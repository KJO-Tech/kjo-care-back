package kjo.care.msvc_blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private Long id;
    private Long blogId;
    private UserInfoDto userId;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate commentDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate modifiedDate;

    private Long commentParentId;
}
