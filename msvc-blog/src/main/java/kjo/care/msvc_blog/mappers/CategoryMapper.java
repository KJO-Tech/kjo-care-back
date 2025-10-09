package kjo.care.msvc_blog.mappers;

import kjo.care.msvc_blog.dto.CategoryDtos.CategoryRequestDto;
import kjo.care.msvc_blog.dto.CategoryDtos.CategoryResponseDto;
import kjo.care.msvc_blog.entities.Category;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    private final ModelMapper modelMapper;

    public CategoryMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.createTypeMap(CategoryRequestDto.class, Category.class);
    }

    public CategoryResponseDto entityToDto(Category entity) {
        return modelMapper.map(entity, CategoryResponseDto.class);
    }

    public Category dtoToEntity(CategoryRequestDto dto) {
        return modelMapper.map(dto, Category.class);
    }

    public void updateEntityFromDto(CategoryRequestDto dto, Category entity) {
        modelMapper.map(dto, entity);
    }

}
