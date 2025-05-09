package kjo.care.msvc_moodTracking.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodCountDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.MoodStatisticsDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.MoodTrendsAnalysisDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.MoodUserRequestDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.UserMoodDTO;
import kjo.care.msvc_moodTracking.services.MoodUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/user-mood")
@RequiredArgsConstructor
@Validated
@Log4j2
@Tag(name = "Usuario Mood", description = "API para gestionar estados de ánimo de usuarios")
@SecurityRequirement(name = "securityToken")
public class MoodTrackingUserController {
    private final MoodUserService moodUserService;

    @Operation(summary = "Obtener todos los usuarios con sus estados de ánimo",
            description = "Retorna todos los usuarios junto con sus registros de estado de ánimo")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios con sus estados de ánimo")
    @GetMapping("")
    public ResponseEntity<List<UserMoodDTO>> getAllUsersWithMoods() {
        log.info("Solicitud para obtener todos los usuarios con sus estados de ánimo");
        List<UserMoodDTO> usersWithMoods = moodUserService.getAllUsersWithMoods();

        log.info("Retornando {} registros de estados de ánimo", usersWithMoods.size());
        return ResponseEntity.ok(usersWithMoods);
    }

    @Operation(summary = "Obtener estadísticas de estados de ánimo",
            description = "Devuelve las estadísticas de los estados de ánimo registrados en un período determinado")
    @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas correctamente")
    @GetMapping("/statistics")
    public Mono<ResponseEntity<MoodStatisticsDto>> getMoodStatistics(
            @RequestParam(defaultValue = "3") @Min(1) @Max(60) int months) {

        log.info("Petición para obtener estadísticas de estados de ánimo de los últimos {} meses", months);

        return moodUserService.getMoodStatistics(months)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @Operation(summary = "Obtener estados de ánimo del usuario autenticado",
            description = "Devuelve todos los estados de ánimo registrados del usuario actual")
    @ApiResponse(responseCode = "200", description = "Estados de ánimo obtenidos correctamente")
    @GetMapping("/my-moods")
    public Mono<ResponseEntity<List<UserMoodDTO>>> getCurrentUserMoods(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        log.info("Petición para obtener estados de ánimo del usuario: {}", userId);

        return moodUserService.getCurrentUserMoods(userId)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(List.of()));
    }


    @Operation(summary = "Registrar estado de ánimo del usuario",
            description = "Registra el estado de ánimo seleccionado por el usuario actual")
    @ApiResponse(responseCode = "201", description = "Estado de ánimo registrado correctamente")
    @ApiResponse(responseCode = "404", description = "Estado de ánimo no encontrado")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @PostMapping("/track-mood")
    public ResponseEntity<UserMoodDTO> trackUserMood(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid MoodUserRequestDto moodUserRequestDto) {

        String userId = jwt.getSubject();
        log.info("Petición para registrar estado de ánimo del usuario: {}, mood id: {}",
                userId, moodUserRequestDto.moodId());

        UserMoodDTO result = moodUserService.trackUserMood(userId, moodUserRequestDto);

        log.info("Estado de ánimo registrado correctamente para el usuario: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Obtener análisis de tendencias de estado de ánimo",
            description = "Devuelve un análisis detallado de las tendencias en los estados de ánimo registrados")
    @ApiResponse(responseCode = "200", description = "Análisis obtenido correctamente")
    @GetMapping("/trends-analysis")
    public Mono<ResponseEntity<MoodTrendsAnalysisDto>> getMoodTrendsAnalysis(
            @RequestParam(defaultValue = "6") @Min(1) @Max(60) int months) {

        log.info("Petición para obtener análisis de tendencias de estados de ánimo de los últimos {} meses", months);

        return moodUserService.getMoodTrendsAnalysis(months)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @Operation(summary = "Obtener cantidad total de registros de estados de ánimo",
            description = "Devuelve el número total de registros de estado de ánimo")
    @ApiResponse(responseCode = "200", description = "Conteo obtenido correctamente")
    @GetMapping("/count")
    public ResponseEntity<MoodCountDto> getMoodCount() {
        log.info("Petición para obtener cantidad total de estados de ánimo");
        Long count = moodUserService.countMoods();
        log.info("Total de estados de ánimo: {}", count);
        return ResponseEntity.ok(new MoodCountDto(count));
    }

    @Operation(summary = "Obtener cantidad de registros de estados de ánimo del mes anterior",
            description = "Devuelve el número de registros de estado de ánimo del mes anterior")
    @ApiResponse(responseCode = "200", description = "Conteo del mes anterior obtenido correctamente")
    @GetMapping("/count/previous-month")
    public ResponseEntity<MoodCountDto> getPreviousMonthMoods() {
        log.info("Petición para obtener cantidad de estados de ánimo del mes anterior");
        Long count = moodUserService.countMoodsPreviousMonth();
        log.info("Total de estados de ánimo del mes anterior: {}", count);
        return ResponseEntity.ok(new MoodCountDto(count));
    }
}