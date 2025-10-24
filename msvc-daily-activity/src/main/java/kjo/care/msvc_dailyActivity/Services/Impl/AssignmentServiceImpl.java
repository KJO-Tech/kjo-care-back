package kjo.care.msvc_dailyActivity.Services.Impl;

import kjo.care.msvc_dailyActivity.DTOs.AssignmentResponseDTO;
import kjo.care.msvc_dailyActivity.Entities.DailyExercise;
import kjo.care.msvc_dailyActivity.Entities.UserExerciseAssignment;
import kjo.care.msvc_dailyActivity.Exceptions.ResourceNotFoundException;
import kjo.care.msvc_dailyActivity.Mappers.AssignmentMapper;
import kjo.care.msvc_dailyActivity.Repositories.DailyExerciseRepository;
import kjo.care.msvc_dailyActivity.Repositories.UserCategorySubscriptionRepository;
import kjo.care.msvc_dailyActivity.Repositories.UserExerciseAssignmentRepository;
import kjo.care.msvc_dailyActivity.Services.IAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentServiceImpl implements IAssignmentService {

    private final UserExerciseAssignmentRepository assignmentRepository;
    private final DailyExerciseRepository exerciseRepository;
    private final UserCategorySubscriptionRepository subscriptionRepository;
    private final AssignmentMapper assignmentMapper;

    @Override
    @Transactional
    public AssignmentResponseDTO assignExercise(String userId, UUID exerciseId) {
        log.info("Asignando ejercicio {} al usuario {}", exerciseId, userId);

        DailyExercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Ejercicio", "id", exerciseId));

        UUID categoryId = exercise.getCategory().getId();
        boolean isSubscribed = subscriptionRepository.existsByUserIdAndCategoryId(userId, categoryId);

        if (!isSubscribed) {
            throw new IllegalArgumentException("Debes estar suscrito a la categoría '"
                    + exercise.getCategory().getName() + "' para recibir este ejercicio");
        }

        UserExerciseAssignment assignment = assignmentMapper.toEntity(userId, exercise);
        UserExerciseAssignment savedAssignment = assignmentRepository.save(assignment);

        log.info("Ejercicio {} asignado exitosamente al usuario {}", exerciseId, userId);
        return assignmentMapper.toResponseDTO(savedAssignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getMyExercisesByDate(String userId, LocalDate date) {
        log.info("Obteniendo ejercicios del usuario {} para la fecha {}", userId, date);

        return assignmentRepository.findByUserIdAndDate(userId, date)
                .stream()
                .map(assignmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getMyPendingExercises(String userId) {
        log.info("Obteniendo ejercicios pendientes del usuario {}", userId);

        return assignmentRepository.findPendingExercises(userId)
                .stream()
                .map(assignmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getMyCompletedExercises(String userId) {
        log.info("Obteniendo ejercicios completados del usuario {}", userId);

        return assignmentRepository.findCompletedExercises(userId)
                .stream()
                .map(assignmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AssignmentResponseDTO markAsCompleted(String userId, UUID assignmentId, Boolean completed) {
        log.info("Usuario {} marcando ejercicio {} como {}", userId, assignmentId,
                completed ? "completado" : "pendiente");

        UserExerciseAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación", "id", assignmentId));

        if (!assignment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Esta asignación no te pertenece");
        }

        assignment.setCompleted(completed);

        UserExerciseAssignment updatedAssignment = assignmentRepository.save(assignment);

        log.info("Ejercicio {} marcado exitosamente como {}", assignmentId,
                completed ? "completado" : "pendiente");
        return assignmentMapper.toResponseDTO(updatedAssignment);
    }

    @Override
    @Transactional
    public void deleteAssignment(String userId, UUID assignmentId) {
        log.info("Usuario {} eliminando asignación {}", userId, assignmentId);

        UserExerciseAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación", "id", assignmentId));

        if (!assignment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Esta asignación no te pertenece");
        }

        assignmentRepository.delete(assignment);
        log.info("Asignación {} eliminada exitosamente", assignmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMyExercises(String userId) {
        return assignmentRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMyCompletedExercises(String userId) {
        return assignmentRepository.countByUserIdAndCompleted(userId, true);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMyPendingExercises(String userId) {
        return assignmentRepository.countByUserIdAndCompleted(userId, false);
    }
}