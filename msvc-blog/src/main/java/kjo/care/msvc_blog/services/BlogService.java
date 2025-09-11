package kjo.care.msvc_blog.services;

import kjo.care.msvc_blog.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface BlogService {
    List<BlogOverviewDto> findAllBlogs();
    List<BlogResponseDto> findAllBlogsPublished();
    BlogPageResponseDto findBlogs(int page, int size);
    BlogResponseDto findBlogById(Long id);
    BlogDetailsDto findBlogDetails(Long id);
    BlogResponseDto saveBlog(BlogRequestDto dto, String userId);
    BlogResponseDto updateBlog (Long id , BlogRequestDto dto, String authenticatedUserId);
    void deleteBlog(Long id,  String authenticatedUserId);
    Long countBlogs();
    Long countBlogsPreviousMonth();
    List<Object[]> countBlogsByDayBetweenDates(String state, LocalDate startDate, LocalDate endDate);

}
