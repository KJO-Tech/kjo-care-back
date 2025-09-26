package kjo.care.msvc_blog.services;

import kjo.care.msvc_blog.dto.CategoryRequestDto;
import kjo.care.msvc_blog.dto.CategoryResponseDto;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryResponseDto> findAllCategories();
    CategoryResponseDto findCategoryById(UUID id);
    CategoryResponseDto saveCategory(CategoryRequestDto dto);
    CategoryResponseDto updateCategory (UUID id , CategoryRequestDto dto);
    void deleteCategory(UUID id);
}
