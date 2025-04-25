package kjo.care.msvc_blog.services;

import kjo.care.msvc_blog.dto.ReactionRequestDto;
import kjo.care.msvc_blog.dto.ReactionResponseDto;

import java.util.List;

public interface ReactionService {
    List<ReactionResponseDto> findAllReactions();
    ReactionResponseDto saveReaction(ReactionRequestDto dto, String userId);
    void deleteReaction(Long id,  String authenticatedUserId);
}
