package kjo.care.msvc_blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class BlogOverviewDto {
    private BlogResponseDto blog;
    private Long reactionCount;
    private Long commentCount;
}
