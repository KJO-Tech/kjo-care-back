package com.analytics.controllers;

import com.analytics.DTOs.*;
import com.analytics.services.AnalyticsService;
import com.analytics.utils.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/")
@Validated
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "API para métricas y estadísticas")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/test")
    public String test() {
        return "Analytics service is up and running!";
    }

    @Operation(summary = "Obtener estadísticas del dashboard", description = "Devuelve estadísticas generales para el dashboard")
    @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas correctamente")
    @GetMapping("/dashboard-stats")
    public Mono<ResponseEntity<ApiResponseDto<DashboardStatsDto>>> getDashboardStats() {
        log.info("Petición para obtener estadísticas del dashboard");

        return analyticsService.getDashboardStats()
                .map(stats -> ResponseBuilder.buildResponse(HttpStatus.OK, "Estadísticas obtenidas correctamente", true, stats))
                .switchIfEmpty(Mono.just(
                        ResponseBuilder.buildResponse(HttpStatus.NO_CONTENT, "No hay estadísticas disponibles", true, null)
                ));
    }

    @Operation(summary = "Obtener conteo diario de blogs del mes actual", description = "Devuelve el número de blogs publicados por día durante el mes actual")
    @ApiResponse(responseCode = "200", description = "Conteo diario obtenido correctamente")
    @GetMapping("/blogs/daily-current-month")
    public Mono<ResponseEntity<ApiResponseDto<List<DailyBlogCountDto>>>> getDailyBlogsCurrentMonth() {
        log.info("Petición para obtener cantidad de blogs por día del mes actual");
        return analyticsService.getDailyBlogsCurrentMonth()
                .map(stats -> ResponseBuilder.buildResponse(HttpStatus.OK, "Estadísticas obtenidas correctamente", true, stats))
                .switchIfEmpty(Mono.just(
                        ResponseBuilder.buildResponse(HttpStatus.NO_CONTENT, "No hay estadísticas disponibles", true, null)
                ));
    }

    @Operation(summary = "Obtener conteo diario de usuarios con estados de ánimo del último mes", description = "Devuelve el número de usuarios distintos que registraron su estado de ánimo por día durante el último mes")
    @ApiResponse(responseCode = "200", description = "Conteo diario obtenido correctamente")
    @GetMapping("/moods/daily-users-last-month")
    public Mono<ResponseEntity<ApiResponseDto<List<DailyMoodUserCountDto>>>> getDailyMoodUsersLastMonth() {
        log.info("Petición para obtener cantidad de usuarios con estados de ánimo por día del último mes");
        return analyticsService.getDailyMoodUsersLastMonth()
                .map(stats -> ResponseBuilder.buildResponse(HttpStatus.OK, "Estadísticas obtenidas correctamente", true, stats))
                .switchIfEmpty(Mono.just(
                        ResponseBuilder.buildResponse(HttpStatus.NO_CONTENT, "No hay estadísticas disponibles", true, null)
                ));
    }

    @Operation(
            summary = "Obtener resumen de analíticas",
            description = "Devuelve un resumen de los logros del blog y los días de registro de estado de ánimo"
    )
    @ApiResponse(responseCode = "200", description = "Resumen de analíticas obtenido correctamente")
    @GetMapping("/summary")
    public Mono<ResponseEntity<AnalyticsSummaryDto>> getAnalyticsSummary(
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return analyticsService.getAnalyticsSummary(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Obtener resumen de analíticas",
            description = "Devuelve un resumen de datos para el dashboard"
    )
    @ApiResponse(responseCode = "200", description = "Resumen de analíticas obtenido correctamente")
    @GetMapping("/summary-dashboard")
    public Mono<ResponseEntity<DashboardSummaryDTO>> getDashboardSummary(
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return analyticsService.getDashboardSummary(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}