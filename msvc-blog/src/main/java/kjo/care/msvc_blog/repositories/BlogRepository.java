package kjo.care.msvc_blog.repositories;

import kjo.care.msvc_blog.entities.Blog;
import kjo.care.msvc_blog.enums.BlogState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    @Query("SELECT b FROM Blog b LEFT JOIN FETCH b.category")
    List<Blog> findAllWithCategory();

    @Query("SELECT b FROM Blog b LEFT JOIN FETCH b.category WHERE b.state = :state")
    List<Blog> findByStateWithCategory(@Param("state") BlogState state);

    @Query("SELECT b FROM Blog b LEFT JOIN FETCH b.category WHERE b.state = :state")
    Page<Blog> findByStateWithCategory(@Param("state") BlogState state, Pageable pageable);

    @Query("SELECT b FROM Blog b LEFT JOIN FETCH b.category WHERE b.id = :id")
    Optional<Blog> findByIdWithCategory(@Param("id") Long id);
}
