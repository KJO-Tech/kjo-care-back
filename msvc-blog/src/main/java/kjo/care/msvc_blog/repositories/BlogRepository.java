package kjo.care.msvc_blog.repositories;

import kjo.care.msvc_blog.entities.Blog;
import kjo.care.msvc_blog.enums.BlogState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Page<Blog> findByState(BlogState state, Pageable pageable);
}
