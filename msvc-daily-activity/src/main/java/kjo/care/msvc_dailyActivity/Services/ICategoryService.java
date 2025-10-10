package kjo.care.msvc_dailyActivity.Services;

import kjo.care.msvc_dailyActivity.DTOs.CategoryRequestDTO;
import kjo.care.msvc_dailyActivity.DTOs.CategoryResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ICategoryService {
    List<CategoryResponseDTO> getAllCategories();
    CategoryResponseDTO getCategoryById(UUID id);
    CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO);
    CategoryResponseDTO updateCategory(UUID id, CategoryRequestDTO requestDTO);
    void deleteCategory(UUID id);
}
