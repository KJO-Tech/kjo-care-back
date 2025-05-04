package com.analytics.controllers;

import com.analytics.DTOs.DashboardStatsDto;
import com.analytics.services.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/analytics")
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

    @Operation(summary = "Obtener estadísticas del dashboard",
            description = "Devuelve estadísticas generales para el dashboard")
    @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas correctamente")
    @GetMapping("/dashboard-stats")
    public Mono<ResponseEntity<DashboardStatsDto>> getDashboardStats() {
        log.info("Petición para obtener estadísticas del dashboard");
        return analyticsService.getDashboardStats()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }
}