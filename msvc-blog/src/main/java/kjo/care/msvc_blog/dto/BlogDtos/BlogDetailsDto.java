package kjo.care.msvc_blog.dto.BlogDtos;

import kjo.care.msvc_blog.dto.CommentDtos.CommentSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogDetailsDto {
    private BlogResponseDto blog;
    private Long reactionCount;
    private Long commentCount;
    private List<CommentSummaryDto> comments;
    private boolean accessible;
}
