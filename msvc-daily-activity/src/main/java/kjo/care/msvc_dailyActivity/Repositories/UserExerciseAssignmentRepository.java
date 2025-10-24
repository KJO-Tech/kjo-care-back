package kjo.care.msvc_dailyActivity.Repositories;

import kjo.care.msvc_dailyActivity.Entities.UserExerciseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserExerciseAssignmentRepository extends JpaRepository<UserExerciseAssignment, UUID> {

    List<UserExerciseAssignment> findByUserId(String userId);

    List<UserExerciseAssignment> findByUserIdAndCompleted(String userId, Boolean completed);

    List<UserExerciseAssignment> findByUserIdOrderByAssignedAtDesc(String userId);

    @Query("SELECT a FROM UserExerciseAssignment a WHERE a.userId = :userId AND a.completed = false ORDER BY a.assignedAt DESC")
    List<UserExerciseAssignment> findPendingExercises(@Param("userId") String userId);

    @Query("SELECT a FROM UserExerciseAssignment a WHERE a.userId = :userId AND a.completed = true ORDER BY a.completedAt DESC")
    List<UserExerciseAssignment> findCompletedExercises(@Param("userId") String userId);

    @Query("SELECT a FROM UserExerciseAssignment a WHERE a.userId = :userId AND a.assignedAt >= :startDate ORDER BY a.assignedAt DESC")
    List<UserExerciseAssignment> findRecentAssignments(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate);

    long countByUserId(String userId);

    long countByUserIdAndCompleted(String userId, Boolean completed);

    boolean existsByUserIdAndExerciseId(String userId, UUID exerciseId);

    @Query("SELECT COUNT(a) FROM UserExerciseAssignment a WHERE a.userId = :userId AND a.completed = true AND a.completedAt >= :startDate")
    long countCompletedInPeriod(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT a FROM UserExerciseAssignment a WHERE a.userId = :userId " +
            "AND CAST(a.assignedAt AS date) = :date " +
            "ORDER BY a.assignedAt DESC")
    List<UserExerciseAssignment> findByUserIdAndDate(@Param("userId") String userId, @Param("date") LocalDate date);
}