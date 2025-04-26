package kjo.care.msvc_blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentSummaryDto {
    private UserInfoDto userId;
    private String content;
    private LocalDate date;
    private List<CommentSummaryDto> childrenComments;
}
