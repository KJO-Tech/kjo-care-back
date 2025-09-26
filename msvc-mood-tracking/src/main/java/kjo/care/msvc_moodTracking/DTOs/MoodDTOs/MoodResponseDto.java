package kjo.care.msvc_moodTracking.DTOs.MoodDTOs;

import lombok.*;

import java.util.UUID;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class MoodResponseDto {
    private UUID id;
    private String name;
    private String description;
//    private String state;
    private String image;
    private String color;
    private Boolean isActive;
}
