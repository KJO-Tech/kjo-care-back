package kjo.care.msvc_dailyActivity.Services;

import kjo.care.msvc_dailyActivity.DTOs.DailyExerciseRequestDTO;
import kjo.care.msvc_dailyActivity.DTOs.DailyExerciseResponseDTO;
import kjo.care.msvc_dailyActivity.Enums.ExerciseContentType;
import kjo.care.msvc_dailyActivity.Enums.ExerciseDifficultyType;

import java.util.List;
import java.util.UUID;

public interface IDailyExerciseService {
    List<DailyExerciseResponseDTO> getOrAssignDailyActivities(String userId);
    List<DailyExerciseResponseDTO> getAllExercises();
    DailyExerciseResponseDTO getExerciseById(UUID id);
    List<DailyExerciseResponseDTO> getExercisesByCategory(UUID categoryId);
    List<DailyExerciseResponseDTO> getExercisesByContentType(ExerciseContentType contentType);
    List<DailyExerciseResponseDTO> getExercisesByDifficulty(ExerciseDifficultyType difficulty);
    List<DailyExerciseResponseDTO> getExercisesByCategoryAndDifficulty(UUID categoryId, ExerciseDifficultyType difficulty);
    DailyExerciseResponseDTO createExercise(DailyExerciseRequestDTO requestDTO);
    DailyExerciseResponseDTO updateExercise(UUID id, DailyExerciseRequestDTO requestDTO);
    void deleteExercise(UUID id);
}
