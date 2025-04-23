package kjo.care.msvc_blog.services.Impl;

import kjo.care.msvc_blog.client.UserClient;
import kjo.care.msvc_blog.dto.BlogRequestDto;
import kjo.care.msvc_blog.dto.BlogResponseDto;

import kjo.care.msvc_blog.dto.UserInfoDto;
import kjo.care.msvc_blog.entities.Blog;
import kjo.care.msvc_blog.entities.Category;
import kjo.care.msvc_blog.enums.BlogState;
import kjo.care.msvc_blog.exceptions.EntityNotFoundException;
import kjo.care.msvc_blog.mappers.BlogMapper;
import kjo.care.msvc_blog.repositories.BlogRepository;
import kjo.care.msvc_blog.repositories.CategoryRepository;
import kjo.care.msvc_blog.services.BlogService;
import kjo.care.msvc_blog.services.IUploadImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Service
@Log4j2
@Validated
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final BlogMapper blogMapper;
    private final UserClient userClient;
    private final IUploadImageService uploadService;

    @Override
    @Transactional(readOnly = true)
    public List<BlogResponseDto> findAllBlogs() {
        return blogRepository.findAll().stream().map(blogMapper::entityToDto).toList();
    }

    @Override
    public List<BlogResponseDto> findAllBlogsPublished() {
        return blogRepository.findAll().stream().filter(blog -> blog.getState().equals(BlogState.PUBLICADO)).map(blogMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#id")
    public BlogResponseDto findBlogById(Long id) {
        Blog blog = findExistBlog(id);
        BlogResponseDto response = blogMapper.entityToDto(blog);
        return response;
    }

    @Override
    @Transactional
    public BlogResponseDto saveBlog(BlogRequestDto dto, String userId) {
        UserInfoDto user = userClient.findUserById(userId);
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        Blog blog = blogMapper.dtoToEntity(dto);
        saveImageOrVideo(dto, blog);
        blog.setState(BlogState.PUBLICADO);
        blog.setCategory(category);
        blog.setUserId(userId);
        blogRepository.save(blog);
        return blogMapper.entityToDto(blog);
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

        blogMapper.updateEntityFromDto(dto, blog);
        blog.setCategory(category);
        deleteImageOrVideo(blog);
        saveImageOrVideo(dto, blog);
        blogRepository.save(blog);
        return blogMapper.entityToDto(blog);
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
        return blogRepository.findById(id).orElseThrow(() -> {
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

}
