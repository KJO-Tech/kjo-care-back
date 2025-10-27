package kjo.care.msvc_dailyActivity.Mappers;

import kjo.care.msvc_dailyActivity.DTOs.AssignmentResponseDTO;
import kjo.care.msvc_dailyActivity.DTOs.ExerciseDetailDTO;
import kjo.care.msvc_dailyActivity.Entities.DailyExercise;
import kjo.care.msvc_dailyActivity.Entities.UserExerciseAssignment;
import org.springframework.stereotype.Component;

@Component
public class AssignmentMapper {

    public UserExerciseAssignment toEntity(String userId, DailyExercise exercise) {
        if (userId == null || exercise == null) {
            return null;
        }

        return UserExerciseAssignment.builder()
                .userId(userId)
                .exercise(exercise)
                .completed(false)
                .build();
    }

    public AssignmentResponseDTO toResponseDTO(UserExerciseAssignment entity) {
        if (entity == null) {
            return null;
        }

        DailyExercise exercise = entity.getExercise();

        ExerciseDetailDTO exerciseDetail = null;
        if (exercise != null) {
            exerciseDetail = ExerciseDetailDTO.builder()
                    .id(exercise.getId())
                    .title(exercise.getTitle())
                    .description(exercise.getDescription())
                    .durationMinutes(exercise.getDurationMinutes())
                    .contentType(exercise.getContentType())
                    .contentTypeDisplay(exercise.getContentType() != null ? exercise.getContentType().getDisplayName() : null)
                    .contentUrl(exercise.getContentUrl())
                    .thumbnailUrl(exercise.getThumbnailUrl())
                    .difficulty(exercise.getDifficulty())
                    .difficultyDisplay(exercise.getDifficulty() != null ? exercise.getDifficulty().getDisplayName() : null)
                    .categoryId(exercise.getCategory() != null ? exercise.getCategory().getId() : null)
                    .categoryName(exercise.getCategory() != null ? exercise.getCategory().getName() : null)
                    .build();
        }

        return AssignmentResponseDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .exercise(exerciseDetail)
                .assignedAt(entity.getAssignedAt())
                .completed(entity.getCompleted())
                .completedAt(entity.getCompletedAt())
                .build();
    }
}