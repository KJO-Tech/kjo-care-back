package kjo.care.msvc_moodTracking.DTOs.MoodDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MoodPageResponseDto(
        @JsonProperty("content")
        List<MoodResponseDto> dto,
        int page,
        long size
) {
}
