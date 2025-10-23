package kjo.care.msvc_dailyActivity.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kjo.care.msvc_dailyActivity.DTOs.ApiResponseDto;
import kjo.care.msvc_dailyActivity.DTOs.DailyExerciseRequestDTO;
import kjo.care.msvc_dailyActivity.DTOs.DailyExerciseResponseDTO;
import kjo.care.msvc_dailyActivity.Enums.ExerciseContentType;
import kjo.care.msvc_dailyActivity.Enums.ExerciseDifficultyType;
import kjo.care.msvc_dailyActivity.Utils.ResponseBuilder;
import kjo.care.msvc_dailyActivity.Services.IDailyExerciseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
@Validated
@Slf4j
@SecurityRequirement(name = "securityToken")
@Tag(name = "Ejercicios Diarios", description = "API para gestionar ejercicios diarios")
public class DailyExerciseController {

    private final IDailyExerciseService dailyExerciseService;

    @Operation(
            summary = "Obtener o asignar actividades diarias para el usuario autenticado",
            description = "Recupera las actividades diarias del usuario para hoy. Si es la primera vez en el día, se las asigna y luego las devuelve.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de actividades diarias obtenida exitosamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @GetMapping("/daily/user")
    public ResponseEntity<ApiResponseDto<List<DailyExerciseResponseDTO>>> getOrAssignDailyActivitiesForUser(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        log.info("GET /exercises/daily/user - Obteniendo o asignando actividades para el usuario {}", userId);
        
        List<DailyExerciseResponseDTO> exercises = dailyExerciseService.getOrAssignDailyActivities(userId);

        log.info("Actividades diarias obtenidas para el usuario {}, total: {}", userId, exercises.size());
        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Actividades diarias obtenidas correctamente",
                true,
                exercises
        );
    }

    @Operation(
            summary = "Obtener ejercicios por dificultad",
            description = "Recupera todos los ejercicios de una dificultad específica",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de ejercicios obtenida exitosamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<ApiResponseDto<List<DailyExerciseResponseDTO>>> getExercisesByDifficulty(
            @Parameter(description = "Dificultad (PRINCIPIANTE, INTERMEDIO, AVANZADO)", required = true)
            @PathVariable ExerciseDifficultyType difficulty) {
        log.info("GET /exercises/difficulty/{} - Obteniendo ejercicios por dificultad", difficulty);
        List<DailyExerciseResponseDTO> exercises = dailyExerciseService.getExercisesByDifficulty(difficulty);
        log.info("Ejercicios obtenidos correctamente para dificultad {}, total: {}", difficulty, exercises.size());
        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Ejercicios obtenidos correctamente",
                true,
                exercises
        );
    }

    @Operation(
            summary = "Obtener ejercicios por categoría y dificultad",
            description = "Recupera todos los ejercicios de una categoría y dificultad específicas",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de ejercicios obtenida exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @GetMapping("/category/{categoryId}/difficulty/{difficulty}")
    public ResponseEntity<ApiResponseDto<List<DailyExerciseResponseDTO>>> getExercisesByCategoryAndDifficulty(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable UUID categoryId,
            @Parameter(description = "Dificultad (PRINCIPIANTE, INTERMEDIO, AVANZADO)", required = true)
            @PathVariable ExerciseDifficultyType difficulty) {
        log.info("GET /exercises/category/{}/difficulty/{} - Obteniendo ejercicios", categoryId, difficulty);
        List<DailyExerciseResponseDTO> exercises = dailyExerciseService.getExercisesByCategoryAndDifficulty(categoryId, difficulty);
        log.info("Ejercicios obtenidos correctamente, total: {}", exercises.size());
        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Ejercicios obtenidos correctamente",
                true,
                exercises
        );
    }

    @Operation(
            summary = "Crear nuevo ejercicio",
            description = "Crea un nuevo ejercicio diario",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Ejercicio creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @PreAuthorize("hasRole('admin_client_role')")
    @PostMapping
    public ResponseEntity<ApiResponseDto<DailyExerciseResponseDTO>> createExercise(
            @Valid @RequestBody DailyExerciseRequestDTO requestDTO) {
        log.info("POST /exercises - Creando ejercicio: {}", requestDTO.getTitle());
        DailyExerciseResponseDTO createdExercise = dailyExerciseService.createExercise(requestDTO);
        log.info("Ejercicio creado exitosamente con ID: {}", createdExercise.getId());
        return ResponseBuilder.buildResponse(
                HttpStatus.CREATED,
                "Ejercicio creado exitosamente",
                true,
                createdExercise
        );
    }

    @Operation(
            summary = "Actualizar ejercicio",
            description = "Actualiza un ejercicio existente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ejercicio actualizado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Ejercicio o categoría no encontrada"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @PreAuthorize("hasRole('admin_client_role')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<DailyExerciseResponseDTO>> updateExercise(
            @Parameter(description = "ID del ejercicio", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody DailyExerciseRequestDTO requestDTO) {
        log.info("PUT /exercises/{} - Actualizando ejercicio", id);
        DailyExerciseResponseDTO updatedExercise = dailyExerciseService.updateExercise(id, requestDTO);
        log.info("Ejercicio actualizado exitosamente con ID: {}", id);
        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Ejercicio actualizado exitosamente",
                true,
                updatedExercise
        );
    }

    @Operation(
            summary = "Eliminar ejercicio",
            description = "Elimina un ejercicio existente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ejercicio eliminado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Ejercicio no encontrado"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @PreAuthorize("hasRole('admin_client_role')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteExercise(
            @Parameter(description = "ID del ejercicio", required = true)
            @PathVariable UUID id) {
        log.info("DELETE /exercises/{} - Eliminando ejercicio", id);
        dailyExerciseService.deleteExercise(id);
        log.info("Ejercicio eliminado exitosamente con ID: {}", id);
        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Ejercicio eliminado exitosamente",
                true,
                null
        );
    }
}