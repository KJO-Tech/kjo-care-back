package kjo.care.msvc_blog.services.Impl;

import kjo.care.msvc_blog.client.UserClient;
import kjo.care.msvc_blog.dto.ReactionRequestDto;
import kjo.care.msvc_blog.dto.ReactionResponseDto;
import kjo.care.msvc_blog.dto.UserInfoDto;
import kjo.care.msvc_blog.entities.Blog;
import kjo.care.msvc_blog.entities.Reaction;
import kjo.care.msvc_blog.enums.ReactionType;
import kjo.care.msvc_blog.exceptions.ConflictException;
import kjo.care.msvc_blog.exceptions.EntityNotFoundException;
import kjo.care.msvc_blog.mappers.ReactionMapper;
import kjo.care.msvc_blog.repositories.BlogRepository;
import kjo.care.msvc_blog.repositories.ReactionRepository;
import kjo.care.msvc_blog.services.ReactionService;
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
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final BlogRepository blogRepository;
    private final UserClient userClient;
    private final ReactionMapper reactionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ReactionResponseDto> findAllReactions() {
        List<Reaction> reactions = reactionRepository.findAll();
        return reactionMapper.entitiesToDtos(reactions);
    }

    @Override
    @Transactional
    public ReactionResponseDto saveReaction(ReactionRequestDto dto, String userId) {
        UserInfoDto user = userClient.findUserById(userId);

        Blog blog = blogRepository.findById(dto.getBlogId())
                .orElseThrow(() -> new EntityNotFoundException("Blog no encontrado"));

        boolean hasReaction = reactionRepository.existsByUserIdAndBlogId(userId, dto.getBlogId());
        if (hasReaction) {
            throw new ConflictException("El usuario ya tiene una reacción para este blog");
        }
        Reaction reaction = reactionMapper.dtoToEntity(dto);
        reaction.setBlog(blog);
        reaction.setUserId(userId);
        reaction.setType(ReactionType.LIKE);
        reaction.setReactionDate(LocalDate.now());
        reactionRepository.save(reaction);
        return reactionMapper.entityToDto(reaction);
    }

    @Override
    public void deleteReaction(Long blogId, String authenticatedUserId) {
        Reaction reaction = reactionRepository.findByUserIdAndBlogId(authenticatedUserId, blogId)
                .orElseThrow(() -> new EntityNotFoundException("Reacción no encontrada"));

        boolean isAdmin = isAdminFromJwt();
        if (!isAdmin && !reaction.getUserId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("Acción no permitida");
        }
        reactionRepository.delete(reaction);
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

    private Reaction findExistReaction(Long id) {
        return reactionRepository.findById(id).orElseThrow(() -> {
            return new EntityNotFoundException("Reaction con id :" + id + " no encontrado");
        });
    }
}
