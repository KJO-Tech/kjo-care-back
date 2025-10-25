package kjo.care.msvc_dailyActivity.Services.Impl;

import kjo.care.msvc_dailyActivity.DTOs.AssignmentResponseDTO;
import kjo.care.msvc_dailyActivity.DTOs.DailyActivitySummaryDTO;
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
import java.util.ArrayList;
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
    @Transactional
    public List<AssignmentResponseDTO> getMyExercisesByDate(String userId, LocalDate date) {
        log.info("Obteniendo ejercicios del usuario {} para la fecha {}", userId, date);
        List<UserExerciseAssignment> assignments = assignmentRepository.findByUserIdAndDate(userId, date);
        if (assignments.isEmpty()) {
            log.info("No se encontraron ejercicios para {}. Asignando 5 nuevos ejercicios diarios.", userId);
            return assignDailyExercises(userId, date);
        }
        return assignments.stream()
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


    @Override
    @Transactional
    public DailyActivitySummaryDTO getDailyActivitySummary(String userId, LocalDate date) {
        log.info("Obteniendo resumen de actividad diaria para el usuario {} en la fecha {}", userId, date);

        List<AssignmentResponseDTO> assignments = getMyExercisesByDate(userId, date);
        if (assignments.isEmpty()) {
            log.info("No se encontraron ejercicios para {}. Asignando 5 nuevos ejercicios diarios.", userId);
            assignments = assignDailyExercises(userId, date);
        }

        long totalAssignments = assignments.size();
        long completedAssignments = assignments.stream().filter(AssignmentResponseDTO::getCompleted).count();

        return DailyActivitySummaryDTO.builder()
                .totalAssignments(totalAssignments)
                .completedAssignments(completedAssignments)
                .build();
    }

    private List<AssignmentResponseDTO> assignDailyExercises(String userId, LocalDate date) {
        List<DailyExercise> randomExercises = exerciseRepository.findRandomExercises(5);
        if (randomExercises.isEmpty()) {
            log.warn("No hay ejercicios disponibles en la base de datos para asignar.");
            return new ArrayList<>();
        }

        List<UserExerciseAssignment> newAssignments = randomExercises.stream()
                .map(exercise -> {
                    UserExerciseAssignment newAssignment = new UserExerciseAssignment();
                    newAssignment.setUserId(userId);
                    newAssignment.setExercise(exercise);
                    newAssignment.setAssignedAt(date.atStartOfDay());
                    newAssignment.setCompleted(false);
                    return newAssignment;
                })
                .collect(Collectors.toList());

        List<UserExerciseAssignment> savedAssignments = assignmentRepository.saveAll(newAssignments);
        log.info("Se asignaron {} nuevos ejercicios al usuario {} para la fecha {}", savedAssignments.size(), userId, date);

        return savedAssignments.stream()
                .map(assignmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
