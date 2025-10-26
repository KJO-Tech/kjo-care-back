package kjo.care.msvc_moodTracking.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import kjo.care.msvc_moodTracking.DTOs.ApiResponseDto;
import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodCountDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.MoodStatisticsDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.MoodTrendsAnalysisDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.MoodUserRequestDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.UserMoodDTO;
import kjo.care.msvc_moodTracking.services.MoodUserService;
import kjo.care.msvc_moodTracking.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user-mood")
@RequiredArgsConstructor
@Validated
@Log4j2
@Tag(name = "Usuario Mood", description = "API para gestionar estados de ánimo de usuarios")
@SecurityRequirement(name = "securityToken")
public class MoodTrackingUserController {
        private final MoodUserService moodUserService;

        @Operation(summary = "Obtener todos los usuarios con sus estados de ánimo", description = "Retorna todos los usuarios junto con sus registros de estado de ánimo")
        @ApiResponse(responseCode = "200", description = "Lista de usuarios con sus estados de ánimo")
        @GetMapping("")
        public ResponseEntity<ApiResponseDto<List<UserMoodDTO>>> getAllUsersWithMoods() {
                log.info("Solicitud para obtener todos los usuarios con sus estados de ánimo");
                List<UserMoodDTO> usersWithMoods = moodUserService.getAllUsersWithMoods();

                log.info("Retornando {} registros de estados de ánimo", usersWithMoods.size());
                return ResponseBuilder.buildResponse(HttpStatus.OK, "Usuarios con estados de ánimo obtenidos correctamente", true, usersWithMoods);
        }

        @Operation(summary = "Obtener estadísticas de estados de ánimo", description = "Devuelve las estadísticas de los estados de ánimo registrados en un período determinado")
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas correctamente")
        @GetMapping("/statistics")
        public Mono<ResponseEntity<ApiResponseDto<MoodStatisticsDto>>> getMoodStatistics(
                        @RequestParam(defaultValue = "3") @Min(1) @Max(60) int months) {

                log.info("Petición para obtener estadísticas de estados de ánimo de los últimos {} meses", months);

            return moodUserService.getMoodStatistics(months)
                    .map(stats -> ResponseBuilder.buildResponse(HttpStatus.OK, "Estadísticas obtenidas correctamente", true, stats))
                    .switchIfEmpty(Mono.just(
                            ResponseBuilder.buildResponse(HttpStatus.NO_CONTENT, "No hay estadísticas disponibles", true, null)
                    ));
        }

        @Operation(summary = "Obtener estados de ánimo del usuario autenticado", description = "Devuelve todos los estados de ánimo registrados del usuario actual")
        @ApiResponse(responseCode = "200", description = "Estados de ánimo obtenidos correctamente")
        @GetMapping("/my-moods")
        public Mono<ResponseEntity<ApiResponseDto<List<UserMoodDTO>>>> getCurrentUserMoods(@AuthenticationPrincipal Jwt jwt) {
                String userId = jwt.getSubject();
                log.info("Petición para obtener estados de ánimo del usuario: {}", userId);

                return moodUserService.getCurrentUserMoods(userId)
                                .collectList()
                                .map(stats -> ResponseBuilder.buildResponse(HttpStatus.OK, "Estadísticas obtenidas correctamente", true, stats))
                                .switchIfEmpty(Mono.just(
                                    ResponseBuilder.buildResponse(HttpStatus.NO_CONTENT, "No hay estadísticas disponibles", true, null)
                                ));
        }

        @Operation(summary = "Registrar estado de ánimo del usuario", description = "Registra el estado de ánimo seleccionado por el usuario actual")
        @ApiResponse(responseCode = "201", description = "Estado de ánimo registrado correctamente")
        @ApiResponse(responseCode = "404", description = "Estado de ánimo no encontrado")
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
        @PostMapping("/track-mood")
        public ResponseEntity<ApiResponseDto<UserMoodDTO>> trackUserMood(
                        @AuthenticationPrincipal Jwt jwt,
                        @RequestBody @Valid MoodUserRequestDto moodUserRequestDto) {

                String userId = jwt.getSubject();
                log.info("Petición para registrar estado de ánimo del usuario: {}, mood id: {}",
                                userId, moodUserRequestDto.moodId());

                UserMoodDTO result = moodUserService.trackUserMood(userId, moodUserRequestDto);

                log.info("Estado de ánimo registrado correctamente para el usuario: {}", userId);
                return ResponseBuilder.buildResponse(HttpStatus.CREATED, "Estado de ánimo registrado correctamente", true, result);
        }

        @Operation(summary = "Obtener análisis de tendencias de estado de ánimo", description = "Devuelve un análisis detallado de las tendencias en los estados de ánimo registrados")
        @ApiResponse(responseCode = "200", description = "Análisis obtenido correctamente")
        @GetMapping("/trends-analysis")
        public Mono<ResponseEntity<ApiResponseDto<MoodTrendsAnalysisDto>>> getMoodTrendsAnalysis(
                        @RequestParam(defaultValue = "6") @Min(1) @Max(60) int months) {

                log.info("Petición para obtener análisis de tendencias de estados de ánimo de los últimos {} meses",
                                months);

                return moodUserService.getMoodTrendsAnalysis(months)
                        .map(stats -> ResponseBuilder.buildResponse(HttpStatus.OK, "Estadísticas obtenidas correctamente", true, stats))
                        .switchIfEmpty(Mono.just(
                                ResponseBuilder.buildResponse(HttpStatus.NO_CONTENT, "No hay estadísticas disponibles", true, null)
                        ));
        }

        @Operation(summary = "Obtener cantidad total de registros de estados de ánimo", description = "Devuelve el número total de registros de estado de ánimo")
        @ApiResponse(responseCode = "200", description = "Conteo obtenido correctamente")
        @GetMapping("/count")
        public ResponseEntity<MoodCountDto> getMoodCount() {
            log.info("Petición para obtener cantidad total de estados de ánimo");
            Long count = moodUserService.countMoods();
            log.info("Total de estados de ánimo: {}", count);
            return ResponseEntity.ok(new MoodCountDto(count));
        }

        @Operation(summary = "Obtener cantidad de registros de estados de ánimo del mes anterior", description = "Devuelve el número de registros de estado de ánimo del mes anterior")
        @ApiResponse(responseCode = "200", description = "Conteo del mes anterior obtenido correctamente")
        @GetMapping("/count/previous-month")
        public ResponseEntity<MoodCountDto> getPreviousMonthMoods() {
            log.info("Petición para obtener cantidad de estados de ánimo del mes anterior");
            Long count = moodUserService.countMoodsPreviousMonth();
            log.info("Total de estados de ánimo del mes anterior: {}", count);
            return ResponseEntity.ok(new MoodCountDto(count));
        }

        @Operation(summary = "Obtener conteo de usuarios por día del último mes", description = "Devuelve el número de usuarios que registraron su estado de ánimo por día durante el último mes")
        @ApiResponse(responseCode = "200", description = "Conteo por día obtenido correctamente")
        @GetMapping("/count-by-day")
        public ResponseEntity<List<Map<String, Object>>> getUserCountsByDay() {
                log.info("Petición para obtener conteo de usuarios por día en el último mes");

                List<Object[]> results = moodUserService.countUsersByDayInLastMonth();

                List<Map<String, Object>> response = new ArrayList<>();
                for (Object[] result : results) {
                        Map<String, Object> entry = new HashMap<>();
                        entry.put("date", result[0]);
                        entry.put("count", result[1]);
                        response.add(entry);
                }

                log.info("Retornando conteo de usuarios por día para {} días", response.size());
            return ResponseEntity.ok(response);
        }
}