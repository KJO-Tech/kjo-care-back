package kjo.care.msvc_blog.repositories;

import kjo.care.msvc_blog.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT r.blog.id, COUNT(r) FROM Reaction r WHERE r.blog.id IN :blogIds GROUP BY r.blog.id")
    List<Object[]> countByBlogIds(@Param("blogIds") List<Long> blogIds);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.blog.id = :blogId")
    Long countByBlogId(@Param("blogId") Long blogId);

    List<Comment> findByBlogIdAndParentIsNull(Long blogId);
    List<Comment> findByParentId(Long parentId);
}
