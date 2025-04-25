package kjo.care.msvc_blog.mappers;

import jakarta.annotation.PostConstruct;
import kjo.care.msvc_blog.client.UserClient;
import kjo.care.msvc_blog.dto.*;
import kjo.care.msvc_blog.entities.Comment;
import kjo.care.msvc_blog.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;
    private final UserClient userClient;

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(Comment.class, CommentResponseDto.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getBlog().getId(), CommentResponseDto::setBlogId);
                    mapper.map(src -> src.getParent().getId(), CommentResponseDto::setCommentParentId);
                });

        TypeMap<CommentRequestDto, Comment> requestMap = modelMapper.createTypeMap(CommentRequestDto.class, Comment.class);
        requestMap.addMappings(mapper -> {
            mapper.skip(Comment::setId);
            mapper.skip(Comment::setCommentDate);
            mapper.skip(Comment::setBlog);
            mapper.skip(Comment::setModifiedDate);
            mapper.skip(Comment::setParent);
            mapper.skip(Comment::setChildren);
        });
    }

    public CommentResponseDto entityToDto(Comment entity) {
        UserInfoDto userId = userClient.findUserById(entity.getUserId());
        CommentResponseDto dto = modelMapper.map(entity, CommentResponseDto.class);
        dto.setUserId(userId);
        return dto;
    }

    public Comment dtoToEntity(CommentRequestDto dto) {
        return  modelMapper.map(dto, Comment.class);
    }

    public void updateEntityFromDto(CommentRequestDto dto, Comment entity) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(dto, entity);
        entity.setModifiedDate(LocalDate.now());
    }
}
