package kjo.care.msvc_blog.services;

import kjo.care.msvc_blog.dto.BlogDetailsDto;
import kjo.care.msvc_blog.dto.BlogPageResponseDto;
import kjo.care.msvc_blog.dto.BlogRequestDto;
import kjo.care.msvc_blog.dto.BlogResponseDto;

import java.util.List;

public interface BlogService {
    List<BlogResponseDto> findAllBlogs();
    List<BlogResponseDto> findAllBlogsPublished();
    BlogPageResponseDto findBlogs(int page, int size);
    BlogResponseDto findBlogById(Long id);
    BlogDetailsDto findBlogDetails(Long id);
    BlogResponseDto saveBlog(BlogRequestDto dto, String userId);
    BlogResponseDto updateBlog (Long id , BlogRequestDto dto, String authenticatedUserId);
    void deleteBlog(Long id,  String authenticatedUserId);
}
