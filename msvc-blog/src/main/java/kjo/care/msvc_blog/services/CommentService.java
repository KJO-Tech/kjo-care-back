package kjo.care.msvc_blog.services;

import kjo.care.msvc_blog.dto.*;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    List<CommentResponseDto> findAllComments();
    CommentResponseDto saveComment(CommentRequestDto dto, String userId);
    CommentResponseDto updateComment(UUID id, CommentRequestDto dto, String userId);
    void deleteComment(UUID id,  String userId);
}
