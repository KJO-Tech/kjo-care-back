package kjo.care.msvc_blog.repositories;

import kjo.care.msvc_blog.entities.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, UUID> {
    boolean existsByUserIdAndBlogId(String userId, UUID blogId);
    Optional<Reaction> findByUserIdAndBlogId(String userId, UUID blogId);

    @Query("SELECT c.blog.id, COUNT(c) FROM Comment c WHERE c.blog.id IN :blogIds GROUP BY c.blog.id")
    List<Object[]> countByBlogIds(@Param("blogIds") List<UUID> blogIds);

    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.blog.id = :blogId")
    Long countByBlogId(@Param("blogId") UUID blogId);

}
