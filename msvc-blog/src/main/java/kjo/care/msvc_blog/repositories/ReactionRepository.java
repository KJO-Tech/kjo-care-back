package kjo.care.msvc_blog.repositories;

import kjo.care.msvc_blog.entities.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, UUID> {
    boolean existsByUserIdAndBlogId(String userId, UUID blogId);
    Optional<Reaction> findByUserIdAndBlogId(String userId, UUID blogId);

    @Query("SELECT r.blog.id, COUNT(r) FROM Reaction r WHERE r.blog.id IN :blogIds GROUP BY r.blog.id")
    List<Object[]> countByBlogIds(@Param("blogIds") List<UUID> blogIds);

    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.blog.id = :blogId")
    Long countByBlogId(@Param("blogId") UUID blogId);

    @Query("SELECT r.blog.id FROM Reaction r WHERE r.userId = :userId AND r.blog.id IN :blogIds")
    Set<UUID> findLikedBlogIdsByUserIdAndBlogIds(@Param("userId") String userId, @Param("blogIds") List<UUID> blogIds);

}
