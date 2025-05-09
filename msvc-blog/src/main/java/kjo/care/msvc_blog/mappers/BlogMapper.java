package kjo.care.msvc_blog.mappers;


import jakarta.annotation.PostConstruct;
import kjo.care.msvc_blog.client.UserClient;
import kjo.care.msvc_blog.dto.*;
import kjo.care.msvc_blog.entities.Blog;
import kjo.care.msvc_blog.entities.Category;
import kjo.care.msvc_blog.repositories.CategoryRepository;
import kjo.care.msvc_blog.repositories.CommentRepository;
import kjo.care.msvc_blog.repositories.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BlogMapper {

    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
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

    public BlogResponseDto entityToDto(Blog entity, Map<String, UserInfoDto> usersMap) {
        BlogResponseDto dto = modelMapper.map(entity, BlogResponseDto.class);
        dto.setAuthor(usersMap.get(entity.getUserId()));
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

    public List<BlogResponseDto> entitiesToDtos(List<Blog> entities, List<UserInfoDto> users) {
        Map<String, UserInfoDto> usersMap = users.stream()
                .collect(Collectors.toMap(UserInfoDto::getId, Function.identity()));

        return entities.stream()
                .map(entity -> entityToDto(entity, usersMap))
                .toList();
    }

}
