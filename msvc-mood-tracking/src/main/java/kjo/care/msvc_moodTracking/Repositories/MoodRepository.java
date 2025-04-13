package kjo.care.msvc_moodTracking.Repositories;

import kjo.care.msvc_moodTracking.Entities.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoodRepository extends JpaRepository<Mood, Long> {
}
