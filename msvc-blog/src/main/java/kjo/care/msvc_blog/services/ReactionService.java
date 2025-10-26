package kjo.care.msvc_blog.services;

import kjo.care.msvc_blog.dto.ReactionRequestDto;
import kjo.care.msvc_blog.dto.ReactionResponseDto;

import java.util.List;
import java.util.UUID;

public interface ReactionService {
    List<ReactionResponseDto> findAllReactions();
    ReactionResponseDto saveReaction(ReactionRequestDto dto, String userId);
    void deleteReaction(UUID id, String authenticatedUserId);
}
