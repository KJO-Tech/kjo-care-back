package kjo.care.msvc_blog.repositories;

import kjo.care.msvc_blog.entities.Blog;
import kjo.care.msvc_blog.enums.BlogState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Page<Blog> findByState(BlogState state, Pageable pageable);

    Long countByState(BlogState state);

    Long countByStateAndPublishedDateBetween(BlogState state, Date startDate, Date endDate);

    @Query(value = "SELECT DATE(published_date) as fecha, COUNT(*) as cantidad FROM blog " +
            "WHERE state = :state AND published_date BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(published_date) " +
            "ORDER BY fecha", nativeQuery = true)
    List<Object[]> countBlogsByDayBetweenDates(@Param("state") String state, @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}
