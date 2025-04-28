package kjo.care.msvc_blog.repositories;

import kjo.care.msvc_blog.entities.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    boolean existsByUserIdAndBlogId(String userId, Long blogId);
    Optional<Reaction> findByUserIdAndBlogId(String userId, Long blogId);

    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.blog.id = :blogId")
    Long countByBlogId(@Param("blogId") Long blogId);

}
