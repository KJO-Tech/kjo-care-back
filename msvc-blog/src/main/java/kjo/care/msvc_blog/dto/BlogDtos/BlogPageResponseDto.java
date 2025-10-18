package kjo.care.msvc_blog.dto.BlogDtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BlogPageResponseDto(
        @JsonProperty("content")
        List<BlogOverviewDto> dto,
        int page,
        long size
) {
}
