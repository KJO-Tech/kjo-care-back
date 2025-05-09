package kjo.care.msvc_blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Long id;
    private UserInfoDto userId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate commentDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate modifiedDate;
    private String content;
    private LocalDate date;
    private List<CommentSummaryDto> childrenComments;
}
