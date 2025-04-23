package kjo.care.msvc_blog.mappers;


import jakarta.annotation.PostConstruct;
import kjo.care.msvc_blog.client.UserClient;
import kjo.care.msvc_blog.dto.BlogRequestDto;
import kjo.care.msvc_blog.dto.BlogResponseDto;
import kjo.care.msvc_blog.dto.CategoryResponseDto;
import kjo.care.msvc_blog.dto.UserInfoDto;
import kjo.care.msvc_blog.entities.Blog;
import kjo.care.msvc_blog.entities.Category;
import kjo.care.msvc_blog.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class BlogMapper {

    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final UserClient userClient;

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(Category.class, CategoryResponseDto.class)
                .addMapping(Category::getId, CategoryResponseDto::setId)
                .addMapping(Category::getName, CategoryResponseDto::setName);

        TypeMap<BlogRequestDto, Blog> requestMap = modelMapper.createTypeMap(BlogRequestDto.class, Blog.class);
        requestMap.addMappings(mapper -> {
            mapper.skip(Blog::setId);
            mapper.skip(Blog::setPublishedDate);
            mapper.skip(Blog::setModifiedDate);
            mapper.skip(Blog::setCategory);
            mapper.skip(Blog::setState);
        });

    }

    public BlogResponseDto entityToDto(Blog entity) {
        UserInfoDto author = userClient.findUserById(entity.getUserId());
        BlogResponseDto dto = modelMapper.map(entity, BlogResponseDto.class);
        dto.setAuthor(author);
        dto.setCategory(modelMapper.map(entity.getCategory(), CategoryResponseDto.class));
        return dto;
    }

    public Blog dtoToEntity(BlogRequestDto dto) {
        Blog blog = modelMapper.map(dto, Blog.class);
        return blog;
    }

    public void updateEntityFromDto(BlogRequestDto dto, Blog entity) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.typeMap(BlogRequestDto.class, Blog.class)
                .addMappings(mapper -> {
                    mapper.skip(Blog::setImage);
                    mapper.skip(Blog::setVideo);
                });
        modelMapper.map(dto, entity);
        entity.setModifiedDate(LocalDate.now());
    }

}
