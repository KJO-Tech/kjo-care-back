package kjo.care.msvc_dailyActivity.Repositories;

import kjo.care.msvc_dailyActivity.Entities.DailyExercise;
import kjo.care.msvc_dailyActivity.Enums.ExerciseContentType;
import kjo.care.msvc_dailyActivity.Enums.ExerciseDifficultyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DailyExerciseRepository extends JpaRepository<DailyExercise, UUID> {

    List<DailyExercise> findByCategoryId(UUID categoryId);

    List<DailyExercise> findByContentType(ExerciseContentType contentType);

    List<DailyExercise> findByDifficulty(ExerciseDifficultyType difficulty);

    @Query("SELECT e FROM DailyExercise e WHERE e.category.id = :categoryId AND e.difficulty = :difficulty")
    List<DailyExercise> findByCategoryAndDifficulty(@Param("categoryId") UUID categoryId,
                                                    @Param("difficulty") ExerciseDifficultyType difficulty);
}
