package kjo.care.msvc_blog.services;

import kjo.care.msvc_blog.dto.CategoryRequestDto;
import kjo.care.msvc_blog.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDto> findAllCategories();
    CategoryResponseDto findCategoryById(Long id);
    CategoryResponseDto saveCategory(CategoryRequestDto dto);
    CategoryResponseDto updateCategory (Long id , CategoryRequestDto dto);
    void deleteCategory(Long id);
}
