package kjo.care.msvc_blog.services.Impl;

import kjo.care.msvc_blog.client.UserClient;
import kjo.care.msvc_blog.dto.CommentRequestDto;
import kjo.care.msvc_blog.dto.CommentResponseDto;
import kjo.care.msvc_blog.dto.UserInfoDto;
import kjo.care.msvc_blog.entities.Blog;
import kjo.care.msvc_blog.entities.Comment;
import kjo.care.msvc_blog.exceptions.EntityNotFoundException;
import kjo.care.msvc_blog.mappers.CommentMapper;
import kjo.care.msvc_blog.repositories.BlogRepository;
import kjo.care.msvc_blog.repositories.CommentRepository;
import kjo.care.msvc_blog.services.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@Validated
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;
    private final UserClient userClient;
    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> findAllComments() {
        return commentRepository.findAll().stream().map(commentMapper::entityToDto).toList();
    }

    @Override
    @Transactional
    public CommentResponseDto saveComment(CommentRequestDto dto, String userId) {
        UserInfoDto user = userClient.findUserById(userId);

        Blog blog = blogRepository.findById(dto.getBlogId())
                .orElseThrow(() -> new EntityNotFoundException("Blog no encontrado"));

        Comment parent = null;
        if(dto.getCommentParentId() != null){
            parent = commentRepository.findById(dto.getCommentParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Comentario padre no encontrado"));
        }

        Comment comment = commentMapper.dtoToEntity(dto);
        comment.setBlog(blog);
        comment.setUserId(userId);
        comment.setCommentDate(LocalDate.now());
        comment.setModifiedDate(LocalDate.now());
        comment.setParent(parent);
        commentRepository.save(comment);
        return commentMapper.entityToDto(comment);
    }


    @Override
    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto dto, String userId) {
        Comment comment = findExistComment(id);

        boolean isAdmin = isAdminFromJwt();

        if (!isAdmin && !comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("Acción no permitida");
        }

        Blog blog = blogRepository.findById(dto.getBlogId())
                .orElseThrow(() -> new EntityNotFoundException("Blog no encontrado"));

        commentMapper.updateEntityFromDto(dto, comment);
        comment.setBlog(blog);
        blogRepository.save(blog);
        return commentMapper.entityToDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id, String userId) {
        Comment comment = findExistComment(id);
        boolean isAdmin = isAdminFromJwt();
        if (!isAdmin && !comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("Acción no permitida");
        }
        commentRepository.delete(comment);
    }

    private Comment findExistComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> {
            return new EntityNotFoundException("Comment con id :" + id + " no encontrado");
        });
    }

    private boolean isAdminFromJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("kjo-care-client");
                if (clientAccess != null) {
                    List<String> clientRoles = (List<String>) clientAccess.get("roles");
                    return clientRoles != null && clientRoles.contains("admin_client_role");
                }
            }
        }
        return false;
    }
}
