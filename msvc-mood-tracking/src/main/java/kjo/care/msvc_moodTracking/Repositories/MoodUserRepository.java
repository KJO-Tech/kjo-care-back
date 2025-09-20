package kjo.care.msvc_moodTracking.Repositories;

import kjo.care.msvc_moodTracking.Entities.MoodUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MoodUserRepository extends JpaRepository<MoodUser, UUID> {
    List<MoodUser> findByUserId(String userId);

    List<MoodUser> findByRecordedDateAfter(LocalDateTime date);
}