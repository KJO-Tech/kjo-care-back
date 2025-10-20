package kjo.care.msvc_dailyActivity.Mappers;

import kjo.care.msvc_dailyActivity.DTOs.CategoryRequestDTO;
import kjo.care.msvc_dailyActivity.DTOs.CategoryResponseDTO;
import kjo.care.msvc_dailyActivity.Entities.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .build();
    }

    public CategoryResponseDTO toResponseDTO(Category entity) {
        if (entity == null) {
            return null;
        }

        return CategoryResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(CategoryRequestDTO dto, Category entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getImageUrl() != null) {
            entity.setImageUrl(dto.getImageUrl());
        }
    }
}