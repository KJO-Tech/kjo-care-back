package kjo.care.msvc_blog.repositories;

import kjo.care.msvc_blog.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.blog.id = :blogId")
    Long countByBlogId(@Param("blogId") UUID blogId);

    List<Comment> findByBlogIdAndParentIsNull(UUID blogId);
    List<Comment> findByParentId(UUID parentId);
}
