package kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs;

import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMoodDTO {
    private UUID id;
    private UserDTO user;
    private MoodResponseDto mood;
    private LocalDateTime recordedDate;
}
