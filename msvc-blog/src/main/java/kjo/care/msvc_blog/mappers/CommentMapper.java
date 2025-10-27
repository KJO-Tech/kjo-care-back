package kjo.care.msvc_blog.mappers;

import jakarta.annotation.PostConstruct;
import kjo.care.msvc_blog.client.UserClient;
import kjo.care.msvc_blog.dto.*;
import kjo.care.msvc_blog.dto.CommentDtos.CommentRequestDto;
import kjo.care.msvc_blog.dto.CommentDtos.CommentResponseDto;
import kjo.care.msvc_blog.dto.CommentDtos.CommentSummaryDto;
import kjo.care.msvc_blog.entities.Comment;
import kjo.care.msvc_blog.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

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

    public CommentSummaryDto mapComment(Comment comment) {
        UserInfoDto userInfo = userClient.findUserById(comment.getUserId());
        List<Comment> replies = commentRepository.findByParentId(comment.getId());

        List<CommentSummaryDto> mappedReplies = replies.stream()
                .map(this::mapComment)
                .toList();

        return CommentSummaryDto.builder()
                .id(comment.getId())
                .commentDate(comment.getCommentDate())
                .modifiedDate(comment.getModifiedDate())
                .userId(userInfo)
                .content(comment.getContent())
                .date(comment.getCommentDate())
                .childrenComments(mappedReplies)
                .build();
    }

    public List<CommentResponseDto> entitiesToDtos(List<Comment> entities) {
        List<String> userIds = entities.stream()
                .map(Comment::getUserId)
                .distinct()
                .toList();

        List<UserInfoDto> users = userClient.findUsersByIds(userIds);

        return entities.stream()
                .map(entity -> {
                    CommentResponseDto dto = modelMapper.map(entity, CommentResponseDto.class);
                    users.stream()
                            .filter(user -> user.getId().equals(entity.getUserId()))
                            .findFirst()
                            .ifPresent(dto::setUserId);
                    return dto;
                })
                .toList();
    }
}
