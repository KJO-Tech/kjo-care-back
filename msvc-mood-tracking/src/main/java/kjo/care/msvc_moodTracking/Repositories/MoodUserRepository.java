package kjo.care.msvc_moodTracking.Repositories;

import kjo.care.msvc_moodTracking.Entities.MoodUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoodUserRepository extends JpaRepository<MoodUser, Long> {
}
