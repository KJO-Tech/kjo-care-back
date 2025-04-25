package kjo.care.msvc_blog.repositories;

import kjo.care.msvc_blog.entities.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    boolean existsByUserIdAndBlogId(String userId, Long blogId);
    Optional<Reaction> findByUserIdAndBlogId(String userId, Long blogId);
}
