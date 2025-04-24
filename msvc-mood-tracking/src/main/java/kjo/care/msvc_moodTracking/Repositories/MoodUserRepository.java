package kjo.care.msvc_moodTracking.Repositories;

import kjo.care.msvc_moodTracking.Entities.MoodUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoodUserRepository extends JpaRepository<MoodUser, Long> {
    List<MoodUser> findByUserId(String userId);
}