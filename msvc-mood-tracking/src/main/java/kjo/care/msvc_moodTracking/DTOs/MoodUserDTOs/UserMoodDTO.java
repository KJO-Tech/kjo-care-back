package kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs;

import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodResponseDto;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMoodDTO {
    private Long id;
    private UserDTO user;
    private MoodResponseDto mood;
    private LocalDateTime recordedDate;
}
