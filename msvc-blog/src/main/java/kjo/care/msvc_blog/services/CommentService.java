package kjo.care.msvc_blog.services;

import kjo.care.msvc_blog.dto.*;

import java.util.List;

public interface CommentService {
    List<CommentResponseDto> findAllComments();
    CommentResponseDto saveComment(CommentRequestDto dto, String userId);
    CommentResponseDto updateComment(Long id, CommentRequestDto dto, String userId);
    void deleteComment(Long id,  String userId);
}
