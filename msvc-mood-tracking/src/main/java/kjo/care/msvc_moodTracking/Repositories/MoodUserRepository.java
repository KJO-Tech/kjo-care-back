package kjo.care.msvc_moodTracking.Repositories;

import kjo.care.msvc_moodTracking.Entities.MoodUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MoodUserRepository extends JpaRepository<MoodUser, UUID> {
    List<MoodUser> findByUserId(String userId);

    List<MoodUser> findByRecordedDateAfter(LocalDateTime date);
        @Query(value = "SELECT DATE(recorded_date) as fecha, COUNT(DISTINCT user_id) as cantidad " +
            "FROM mood_user " +
            "WHERE recorded_date BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(recorded_date) " +
            "ORDER BY fecha", nativeQuery = true)
    List<Object[]> countDistinctUsersByDayBetweenDates(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
}