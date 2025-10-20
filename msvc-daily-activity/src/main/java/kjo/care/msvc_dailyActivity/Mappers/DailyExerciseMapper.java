package kjo.care.msvc_dailyActivity.Mappers;

import kjo.care.msvc_dailyActivity.DTOs.DailyExerciseRequestDTO;
import kjo.care.msvc_dailyActivity.DTOs.DailyExerciseResponseDTO;
import kjo.care.msvc_dailyActivity.Entities.Category;
import kjo.care.msvc_dailyActivity.Entities.DailyExercise;
import org.springframework.stereotype.Component;

@Component
public class DailyExerciseMapper {

    public DailyExercise toEntity(DailyExerciseRequestDTO dto, Category category) {
        if (dto == null) {
            return null;
        }

        return DailyExercise.builder()
                .category(category)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .durationMinutes(dto.getDurationMinutes())
                .contentType(dto.getContentType())
                .contentUrl(dto.getContentUrl())
                .thumbnailUrl(dto.getThumbnailUrl())
                .difficulty(dto.getDifficulty())
                .build();
    }

    public DailyExerciseResponseDTO toResponseDTO(DailyExercise entity) {
        if (entity == null) {
            return null;
        }

        return DailyExerciseResponseDTO.builder()
                .id(entity.getId())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : null)
                .title(entity.getTitle())
                .description(entity.getDescription())
                .durationMinutes(entity.getDurationMinutes())
                .contentType(entity.getContentType())
                .contentTypeDisplay(entity.getContentType() != null ? entity.getContentType().getDisplayName() : null)
                .contentUrl(entity.getContentUrl())
                .thumbnailUrl(entity.getThumbnailUrl())
                .difficulty(entity.getDifficulty())
                .difficultyDisplay(entity.getDifficulty() != null ? entity.getDifficulty().getDisplayName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(DailyExerciseRequestDTO dto, DailyExercise entity, Category category) {
        if (dto == null || entity == null) {
            return;
        }

        if (category != null) {
            entity.setCategory(category);
        }
        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getDurationMinutes() != null) {
            entity.setDurationMinutes(dto.getDurationMinutes());
        }
        if (dto.getContentType() != null) {
            entity.setContentType(dto.getContentType());
        }
        if (dto.getContentUrl() != null) {
            entity.setContentUrl(dto.getContentUrl());
        }
        if (dto.getThumbnailUrl() != null) {
            entity.setThumbnailUrl(dto.getThumbnailUrl());
        }
        if (dto.getDifficulty() != null) {
            entity.setDifficulty(dto.getDifficulty());
        }
    }
}