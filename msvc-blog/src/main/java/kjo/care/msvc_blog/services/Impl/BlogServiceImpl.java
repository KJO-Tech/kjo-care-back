package kjo.care.msvc_blog.services.Impl;

import kjo.care.msvc_blog.client.UserClient;
import kjo.care.msvc_blog.dto.*;

import kjo.care.msvc_blog.entities.Blog;
import kjo.care.msvc_blog.entities.Category;
import kjo.care.msvc_blog.enums.BlogState;
import kjo.care.msvc_blog.exceptions.EntityNotFoundException;
import kjo.care.msvc_blog.mappers.BlogMapper;
import kjo.care.msvc_blog.mappers.CommentMapper;
import kjo.care.msvc_blog.repositories.BlogRepository;
import kjo.care.msvc_blog.repositories.CategoryRepository;
import kjo.care.msvc_blog.repositories.CommentRepository;
import kjo.care.msvc_blog.repositories.ReactionRepository;
import kjo.care.msvc_blog.services.BlogService;
import kjo.care.msvc_blog.services.IUploadImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Log4j2
@Validated
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
    private final BlogMapper blogMapper;
    private final CommentMapper commentMapper;
    private final UserClient userClient;
    private final IUploadImageService uploadService;

    @Override
    @Transactional(readOnly = true)
    public List<BlogOverviewDto> findAllBlogs() {
        List<Blog> blogs = blogRepository.findAllWithCategory();
        return processBlogsAndBuildOverviews(blogs);
    }

    @Override
    public List<BlogResponseDto> findAllBlogsPublished() {
        List<Blog> publishedBlogs = blogRepository.findByStateWithCategory(BlogState.PUBLICADO);
        List<UserInfoDto> users = fetchUsersForBlogs(publishedBlogs);
        return blogMapper.entitiesToDtos(publishedBlogs, users);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogPageResponseDto findBlogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedDate").descending());
        Page<Blog> publishedBlogsPage = blogRepository.findByStateWithCategory(BlogState.PUBLICADO, pageable);
        List<Blog> publishedBlogs = publishedBlogsPage.getContent();

        List<BlogOverviewDto> blogOverviews = processBlogsAndBuildOverviews(publishedBlogs);

        return new BlogPageResponseDto(
                blogOverviews,
                page,
                publishedBlogsPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "blogs", key = "#id")
    public BlogResponseDto findBlogById(Long id) {
        Blog blog = findExistBlog(id);
        List<Blog> singleBlogList = List.of(blog);
        List<UserInfoDto> users = fetchUsersForBlogs(singleBlogList);
        Map<String, UserInfoDto> usersMap = users.stream()
                .collect(Collectors.toMap(UserInfoDto::getId, Function.identity()));

        return blogMapper.entityToDto(blog, usersMap);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "blogs", key = "#id")
    public BlogDetailsDto findBlogDetails(Long id) {
        Blog blog = findExistBlog(id);

        List<Blog> singleBlogList = List.of(blog);
        List<UserInfoDto> users = fetchUsersForBlogs(singleBlogList);
        Map<String, UserInfoDto> usersMap = users.stream()
                .collect(Collectors.toMap(UserInfoDto::getId, Function.identity()));

        BlogResponseDto blogResponseDto = blogMapper.entityToDto(blog, usersMap);

        boolean isAdmin = isAdminFromJwt();
        boolean isPublished = BlogState.PUBLICADO.equals(blog.getState());

        if (!isAdmin && !isPublished) {
            throw new AccessDeniedException("Acceso denegado: El blog no está publicado");
        }

        Long reactionCount = reactionRepository.countByBlogId(blog.getId());
        Long commentCount = commentRepository.countByBlogId(blog.getId());

        List<CommentSummaryDto> comments = commentRepository.findByBlogIdAndParentIsNull(blog.getId())
                .stream()
                .map(commentMapper::mapComment)
                .toList();

        return BlogDetailsDto.builder()
                .blog(blogResponseDto)
                .reactionCount(reactionCount)
                .commentCount(commentCount)
                .comments(comments)
                .accessible(isPublished)
                .build();
    }

    @Override
    @Transactional
    public BlogResponseDto saveBlog(BlogRequestDto dto, String userId) {
        UserInfoDto user = userClient.findUserById(userId);
        Map<String, UserInfoDto> userMap = new HashMap<>();
        userMap.put(userId, user);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        Blog blog = blogMapper.dtoToEntity(dto);
        saveImageOrVideo(dto, blog);
        blog.setState(BlogState.PUBLICADO);
        blog.setCategory(category);
        blog.setUserId(userId);
        blogRepository.save(blog);
        return blogMapper.entityToDto(blog, userMap);
    }

    @Override
    @Transactional
    public BlogResponseDto updateBlog(Long id, BlogRequestDto dto, String authenticatedUserId) {
        Blog blog = findExistBlog(id);

        boolean isAdmin = isAdminFromJwt();

        if (!isAdmin && !blog.getUserId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("Acción no permitida");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        UserInfoDto user = userClient.findUserById(authenticatedUserId);
        Map<String, UserInfoDto> userMap = new HashMap<>();
        userMap.put(authenticatedUserId, user);

        blogMapper.updateEntityFromDto(dto, blog);
        blog.setCategory(category);
        deleteImageOrVideo(blog);
        saveImageOrVideo(dto, blog);
        blogRepository.save(blog);
        return blogMapper.entityToDto(blog, userMap);
    }

    @Override
    public void deleteBlog(Long id,  String authenticatedUserId) {
        Blog blog = findExistBlog(id);
        boolean isAdmin = isAdminFromJwt();

        if (!isAdmin && !blog.getUserId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("Acción no permitida");
        }

        blog.setState(BlogState.ELIMINADO);
        blogRepository.save(blog);
    }

    private Blog findExistBlog(Long id) {
        return blogRepository.findByIdWithCategory(id).orElseThrow(() -> {
            return new EntityNotFoundException("Blog con id :" + id + " no encontrado");
        });
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

    private void saveImageOrVideo(BlogRequestDto dto, Blog blog) {
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            String imageUrl = uploadService.uploadFile(
                    dto.getImage(),
                    "blog/images",
                    "auto"
            );
            blog.setImage(imageUrl);
        }
        if (dto.getVideo() != null && !dto.getVideo().isEmpty()) {
            String videoUrl = uploadService.uploadFile(
                    dto.getVideo(),
                    "blog/videos",
                    "video"
            );
            blog.setVideo(videoUrl);
        }
    }

    private void deleteImageOrVideo(Blog blog) {
        try {
            if (blog.getImage() != null && blog.getImage().startsWith("http")) {
                uploadService.DeleteImage(blog.getImage(), "image");
            }
            if (blog.getVideo() != null && blog.getVideo().startsWith("http")) {
                uploadService.DeleteImage(blog.getVideo(), "video");
            }
        } catch (Exception e) {
            log.error("Error al eliminar imagen/video: {}", e.getMessage());
        }
    }

    private List<BlogOverviewDto> processBlogsAndBuildOverviews(List<Blog> blogs) {
        List<Long> blogIds = blogs.stream().map(Blog::getId).toList();
        Map<Long, Long> reactionCounts = getReactionCounts(blogIds);
        Map<Long, Long> commentCounts = getCommentCounts(blogIds);

        List<BlogResponseDto> blogDtos = blogMapper.entitiesToDtos(blogs, fetchUsersForBlogs(blogs));

        return blogDtos.stream()
                .map(dto -> buildBlogOverview(dto, reactionCounts, commentCounts))
                .toList();
    }

    private List<UserInfoDto> fetchUsersForBlogs(List<Blog> blogs) {
        List<String> userIds = extractUserIds(blogs);
        return userClient.findUsersByIds(userIds);
    }

    private List<String> extractUserIds(List<Blog> blogs) {
        return blogs.stream()
                .map(Blog::getUserId)
                .distinct()
                .toList();
    }

    private BlogOverviewDto buildBlogOverview(BlogResponseDto dto, Map<Long, Long> reactionCounts, Map<Long, Long> commentCounts) {
        return BlogOverviewDto.builder()
                .blog(dto)
                .reactionCount(reactionCounts.getOrDefault(dto.getId(), 0L))
                .commentCount(commentCounts.getOrDefault(dto.getId(), 0L))
                .build();
    }

    private Map<Long, Long> getReactionCounts(List<Long> blogIds) {
        return reactionRepository.countByBlogIds(blogIds).stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    private Map<Long, Long> getCommentCounts(List<Long> blogIds) {
        return commentRepository.countByBlogIds(blogIds).stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));
    }
}
