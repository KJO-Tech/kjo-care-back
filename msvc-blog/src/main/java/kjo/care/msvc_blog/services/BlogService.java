package kjo.care.msvc_blog.services;

import kjo.care.msvc_blog.dto.BlogDtos.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BlogService {
    List<BlogOverviewDto> findAllBlogs();
    List<BlogResponseDto> findAllBlogsPublished();
    BlogPageResponseDto findBlogs(int page, int size, String userId);
    BlogResponseDto findBlogById(UUID id);
    BlogDetailsDto findBlogDetails(UUID id, String userId);
    BlogResponseDto saveBlog(BlogRequestDto dto, String userId);
    BlogResponseDto updateBlog (UUID id , BlogRequestDto dto, String authenticatedUserId);
    void deleteBlog(UUID id,  String authenticatedUserId);
    void rejectBlog(UUID id, String adminId);
    Long countBlogs();
    Long countBlogsPreviousMonth();
    List<Object[]> countBlogsByDayBetweenDates(String state, LocalDate startDate, LocalDate endDate);

}
