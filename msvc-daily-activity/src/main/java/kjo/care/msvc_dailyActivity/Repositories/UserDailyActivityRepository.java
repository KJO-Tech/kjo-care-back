package kjo.care.msvc_dailyActivity.Repositories;

import kjo.care.msvc_dailyActivity.Entities.UserDailyActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserDailyActivityRepository extends JpaRepository<UserDailyActivity, UUID> {

    boolean existsByUserIdAndAssignedDate(String userId, LocalDate date);

    List<UserDailyActivity> findByUserIdAndAssignedDate(String userId, LocalDate date);
}
