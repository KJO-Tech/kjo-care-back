package kjo.care.msvc_dailyActivity.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kjo.care.msvc_dailyActivity.DTOs.ApiResponseDto;
import kjo.care.msvc_dailyActivity.DTOs.AssignmentRequestDTO;
import kjo.care.msvc_dailyActivity.DTOs.AssignmentResponseDTO;
import kjo.care.msvc_dailyActivity.DTOs.CompleteExerciseRequestDTO;
import kjo.care.msvc_dailyActivity.Services.IAssignmentService;
import kjo.care.msvc_dailyActivity.Utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
@Validated
@Slf4j
@SecurityRequirement(name = "securityToken")
@Tag(name = "Asignaciones de Ejercicios", description = "API para gestionar asignaciones de ejercicios a usuarios")
public class AssignmentController {

    private final IAssignmentService assignmentService;

    @Operation(
            summary = "Asignar ejercicio al usuario",
            description = "Asigna un ejercicio al usuario si está suscrito a la categoría correspondiente",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Ejercicio asignado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Usuario no suscrito a la categoría"),
                    @ApiResponse(responseCode = "404", description = "Ejercicio no encontrado"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @PreAuthorize("hasRole('admin_client_role')")
    @PostMapping
    public ResponseEntity<ApiResponseDto<AssignmentResponseDTO>> assignExercise(
            @Valid @RequestBody AssignmentRequestDTO requestDTO,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("POST /assignments - Asignando ejercicio {} al usuario {}", requestDTO.getExerciseId(), userId);

        AssignmentResponseDTO response = assignmentService.assignExercise(userId, requestDTO.getExerciseId());

        log.info("Ejercicio asignado exitosamente al usuario {}", userId);
        return ResponseBuilder.buildResponse(
                HttpStatus.CREATED,
                "Ejercicio asignado exitosamente",
                true,
                response
        );
    }

    @Operation(
            summary = "Obtener mis ejercicios asignados por fecha",
            description = "Obtiene los ejercicios asignados al usuario para una fecha específica. Si no se especifica fecha, devuelve los de hoy.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ejercicios obtenidos exitosamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @GetMapping("/my-exercises")
    public ResponseEntity<ApiResponseDto<List<AssignmentResponseDTO>>> getMyExercises(
            @Parameter(description = "Fecha en formato YYYY-MM-DD (opcional, por defecto hoy)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        LocalDate searchDate = date != null ? date : LocalDate.now();

        log.info("GET /assignments/my-exercises - Obteniendo ejercicios del usuario {} para fecha {}", userId, searchDate);

        List<AssignmentResponseDTO> exercises = assignmentService.getMyExercisesByDate(userId, searchDate);

        if (exercises.isEmpty()) {
            log.info("Usuario {} no tiene ejercicios asignados para {}", userId, searchDate);
            return ResponseBuilder.buildResponse(
                    HttpStatus.OK,
                    "No tienes ejercicios asignados para esta fecha",
                    true,
                    exercises
            );
        }

        log.info("Usuario {} tiene {} ejercicios para {}", userId, exercises.size(), searchDate);
        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Ejercicios obtenidos correctamente",
                true,
                exercises
        );
    }

    @Operation(
            summary = "Obtener ejercicios pendientes",
            description = "Obtiene los ejercicios pendientes (no completados) del usuario",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ejercicios pendientes obtenidos"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @GetMapping("/pending")
    public ResponseEntity<ApiResponseDto<List<AssignmentResponseDTO>>> getMyPendingExercises(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("GET /assignments/pending - Obteniendo ejercicios pendientes del usuario {}", userId);

        List<AssignmentResponseDTO> exercises = assignmentService.getMyPendingExercises(userId);

        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                exercises.isEmpty() ? "No tienes ejercicios pendientes" : "Ejercicios pendientes obtenidos",
                true,
                exercises
        );
    }

    @Operation(
            summary = "Obtener ejercicios completados",
            description = "Obtiene los ejercicios completados del usuario",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ejercicios completados obtenidos"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @GetMapping("/completed")
    public ResponseEntity<ApiResponseDto<List<AssignmentResponseDTO>>> getMyCompletedExercises(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("GET /assignments/completed - Obteniendo ejercicios completados del usuario {}", userId);

        List<AssignmentResponseDTO> exercises = assignmentService.getMyCompletedExercises(userId);

        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                exercises.isEmpty() ? "No tienes ejercicios completados" : "Ejercicios completados obtenidos",
                true,
                exercises
        );
    }

    @Operation(
            summary = "Marcar ejercicio como completado/pendiente",
            description = "Permite marcar un ejercicio asignado como completado o volver a marcarlo como pendiente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Asignación no encontrada"),
                    @ApiResponse(responseCode = "400", description = "La asignación no pertenece al usuario"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponseDto<AssignmentResponseDTO>> markAsCompleted(
            @Parameter(description = "ID de la asignación", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody CompleteExerciseRequestDTO requestDTO,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("PATCH /assignments/{}/complete - Usuario {} marcando como {}",
                id, userId, requestDTO.getCompleted() ? "completado" : "pendiente");

        AssignmentResponseDTO response = assignmentService.markAsCompleted(userId, id, requestDTO.getCompleted());

        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                requestDTO.getCompleted() ? "Ejercicio marcado como completado" : "Ejercicio marcado como pendiente",
                true,
                response
        );
    }

    @Operation(
            summary = "Eliminar asignación",
            description = "Elimina una asignación de ejercicio del usuario",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asignación eliminada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Asignación no encontrada"),
                    @ApiResponse(responseCode = "400", description = "La asignación no pertenece al usuario"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteAssignment(
            @Parameter(description = "ID de la asignación", required = true)
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("DELETE /assignments/{} - Usuario {}", id, userId);

        assignmentService.deleteAssignment(userId, id);

        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Asignación eliminada exitosamente",
                true,
                null
        );
    }

    @Operation(
            summary = "Contar ejercicios totales",
            description = "Obtiene el número total de ejercicios asignados al usuario",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conteo exitoso"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @GetMapping("/count/total")
    public ResponseEntity<ApiResponseDto<Long>> countMyExercises(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        long count = assignmentService.countMyExercises(userId);

        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Total de ejercicios: " + count,
                true,
                count
        );
    }

    @Operation(
            summary = "Contar ejercicios completados",
            description = "Obtiene el número de ejercicios completados por el usuario",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conteo exitoso"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @GetMapping("/count/completed")
    public ResponseEntity<ApiResponseDto<Long>> countMyCompletedExercises(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        long count = assignmentService.countMyCompletedExercises(userId);

        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Ejercicios completados: " + count,
                true,
                count
        );
    }

    @Operation(
            summary = "Contar ejercicios pendientes",
            description = "Obtiene el número de ejercicios pendientes del usuario",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conteo exitoso"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @GetMapping("/count/pending")
    public ResponseEntity<ApiResponseDto<Long>> countMyPendingExercises(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        long count = assignmentService.countMyPendingExercises(userId);

        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Ejercicios pendientes: " + count,
                true,
                count
        );
    }
}