package kjo.care.msvc_dailyActivity.DTOs;

import kjo.care.msvc_dailyActivity.Enums.ExerciseContentType;
import kjo.care.msvc_dailyActivity.Enums.ExerciseDifficultyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyExerciseResponseDTO {

    private UUID id;
    private UUID categoryId;
    private String categoryName;
    private String title;
    private String description;
    private Integer durationMinutes;
    private ExerciseContentType contentType;
    private String contentTypeDisplay;
    private String contentUrl;
    private String thumbnailUrl;
    private ExerciseDifficultyType difficulty;
    private String difficultyDisplay;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

