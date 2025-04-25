package kjo.care.msvc_blog.services.Impl;

import kjo.care.msvc_blog.client.UserClient;
import kjo.care.msvc_blog.dto.ReactionRequestDto;
import kjo.care.msvc_blog.dto.ReactionResponseDto;
import kjo.care.msvc_blog.dto.UserInfoDto;
import kjo.care.msvc_blog.entities.Blog;
import kjo.care.msvc_blog.entities.Category;
import kjo.care.msvc_blog.entities.Reaction;
import kjo.care.msvc_blog.enums.BlogState;
import kjo.care.msvc_blog.enums.ReactionType;
import kjo.care.msvc_blog.exceptions.EntityNotFoundException;
import kjo.care.msvc_blog.mappers.BlogMapper;
import kjo.care.msvc_blog.mappers.CategoryMapper;
import kjo.care.msvc_blog.mappers.ReactionMapper;
import kjo.care.msvc_blog.repositories.BlogRepository;
import kjo.care.msvc_blog.repositories.CategoryRepository;
import kjo.care.msvc_blog.repositories.ReactionRepository;
import kjo.care.msvc_blog.services.ReactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

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
        return reactionRepository.findAll().stream().map(reactionMapper::entityToDto).toList();
    }

    @Override
    @Transactional
    public ReactionResponseDto saveReaction(ReactionRequestDto dto, String userId) {
        UserInfoDto user = userClient.findUserById(userId);
        Blog blog = blogRepository.findById(dto.getBlogId())
                .orElseThrow(() -> new EntityNotFoundException("Blog no encontrado"));

        Reaction reaction = reactionMapper.dtoToEntity(dto);
        reaction.setBlog(blog);
        reaction.setUserId(userId);
        reaction.setType(ReactionType.valueOf(dto.getType()));
        reactionRepository.save(reaction);
        return reactionMapper.entityToDto(reaction);
    }

    @Override
    @Transactional
    public ReactionResponseDto updateReaction(Long id, ReactionRequestDto dto, String authenticatedUserId) {
        return null;
    }

    @Override
    public void deleteReaction(Long id, String authenticatedUserId) {

    }

    private Reaction findExistReaction(Long id) {
        return reactionRepository.findById(id).orElseThrow(() -> {
            return new EntityNotFoundException("Reaction con id :" + id + " no encontrado");
        });
    }
}
