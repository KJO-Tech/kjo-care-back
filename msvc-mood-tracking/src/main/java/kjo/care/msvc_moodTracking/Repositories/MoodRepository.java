package kjo.care.msvc_moodTracking.Repositories;

import kjo.care.msvc_moodTracking.Entities.MoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MoodRepository extends JpaRepository<MoodEntity, UUID> {
}
