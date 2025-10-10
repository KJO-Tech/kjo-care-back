package kjo.care.msvc_dailyActivity.Services.Impl;

import kjo.care.msvc_dailyActivity.Services.ICategoryService;
import kjo.care.msvc_dailyActivity.DTOs.CategoryRequestDTO;
import kjo.care.msvc_dailyActivity.DTOs.CategoryResponseDTO;
import kjo.care.msvc_dailyActivity.Entities.Category;
import kjo.care.msvc_dailyActivity.Exceptions.ResourceNotFoundException;
import kjo.care.msvc_dailyActivity.Mappers.CategoryMapper;
import kjo.care.msvc_dailyActivity.Repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        log.info("Obteniendo todas las categorías");
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(UUID id) {
        log.info("Obteniendo categoría con ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", id));
        return categoryMapper.toResponseDTO(category);
    }

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {
        log.info("Creando nueva categoría: {}", requestDTO.getName());

        if (categoryRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + requestDTO.getName());
        }

        Category category = categoryMapper.toEntity(requestDTO);
        Category savedCategory = categoryRepository.save(category);
        log.info("Categoría creada exitosamente con ID: {}", savedCategory.getId());

        return categoryMapper.toResponseDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(UUID id, CategoryRequestDTO requestDTO) {
        log.info("Actualizando categoría con ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", id));

        if (!category.getName().equals(requestDTO.getName()) &&
                categoryRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + requestDTO.getName());
        }

        categoryMapper.updateEntityFromDTO(requestDTO, category);
        Category updatedCategory = categoryRepository.save(category);

        log.info("Categoría actualizada exitosamente con ID: {}", id);
        return categoryMapper.toResponseDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        log.info("Eliminando categoría con ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", id));

        categoryRepository.delete(category);
        log.info("Categoría eliminada exitosamente con ID: {}", id);
    }
}

