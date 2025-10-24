package kjo.care.msvc_dailyActivity.Services;

import kjo.care.msvc_dailyActivity.DTOs.AssignmentResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IAssignmentService {

    AssignmentResponseDTO assignExercise(String userId, UUID exerciseId);

    List<AssignmentResponseDTO> getMyExercisesByDate(String userId, LocalDate date);

    List<AssignmentResponseDTO> getMyPendingExercises(String userId);

    List<AssignmentResponseDTO> getMyCompletedExercises(String userId);

    AssignmentResponseDTO markAsCompleted(String userId, UUID assignmentId, Boolean completed);

    void deleteAssignment(String userId, UUID assignmentId);

    long countMyExercises(String userId);

    long countMyCompletedExercises(String userId);

    long countMyPendingExercises(String userId);
}