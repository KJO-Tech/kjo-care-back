package kjo.care.msvc_moodTracking.DTOs;

import lombok.*;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class MoodResponseDto {
    private Long id;
    private String name;
    private String description;
    private String state;
    private String image;
    private String color;
    private Boolean isActive;
}
