package kjo.care.msvc_blog.services;

import kjo.care.msvc_blog.dto.BlogRequestDto;
import kjo.care.msvc_blog.dto.BlogResponseDto;
import kjo.care.msvc_blog.dto.CategoryRequestDto;
import kjo.care.msvc_blog.dto.CategoryResponseDto;

import java.util.List;

public interface BlogService {
    List<BlogResponseDto> findAllBlogs();
    BlogResponseDto findBlogById(Long id);
    BlogResponseDto saveBlog(BlogRequestDto dto, String userId);
    BlogResponseDto updateBlog (Long id , BlogRequestDto dto, String authenticatedUserId);
    void deleteBlog(Long id,  String authenticatedUserId);
}
