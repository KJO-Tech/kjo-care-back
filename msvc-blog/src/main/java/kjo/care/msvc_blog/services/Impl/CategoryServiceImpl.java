package kjo.care.msvc_blog.services.Impl;

import kjo.care.msvc_blog.entities.Category;
import kjo.care.msvc_blog.exceptions.EntityNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import kjo.care.msvc_blog.dto.CategoryRequestDto;
import kjo.care.msvc_blog.dto.CategoryResponseDto;
import kjo.care.msvc_blog.mappers.CategoryMapper;
import kjo.care.msvc_blog.repositories.CategoryRepository;
import kjo.care.msvc_blog.services.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Log4j2
@Validated
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> findAllCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#id")
    public CategoryResponseDto findCategoryById(Long id) {
        Category category = findExistCategory(id);
        CategoryResponseDto response = categoryMapper.entityToDto(category);
        return response;
    }

    @Transactional
    @Override
    public CategoryResponseDto saveCategory(CategoryRequestDto dto) {
        Category category = categoryMapper.dtoToEntity(dto);
        categoryRepository.save(category);
        return categoryMapper.entityToDto(category);
    }

    @Transactional
    @Override
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto) {
        Category category = findExistCategory(id);
        categoryMapper.updateEntityFromDto(dto, category);
        categoryRepository.save(category);
        return categoryMapper.entityToDto(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = findExistCategory(id);
        categoryRepository.delete(category);
    }

    private Category findExistCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> {
            return new EntityNotFoundException("Category con id :" + id + " no encontrado");
        });
    }
}
