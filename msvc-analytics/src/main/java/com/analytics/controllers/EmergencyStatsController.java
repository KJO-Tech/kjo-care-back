package com.analytics.controllers;

import com.analytics.DTOs.ApiResponseDto;
import com.analytics.DTOs.ResourceStatsDto;
import com.analytics.client.EmergencyFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/analytics/resources")
@RequiredArgsConstructor
@Slf4j
public class EmergencyStatsController {

    private final EmergencyFeignClient emergencyFeignClient;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponseDto<ResourceStatsDto>> getResourceStats() {
        try {
            ApiResponseDto<ResourceStatsDto> stats = emergencyFeignClient.getEmergencyStats();
            return ResponseEntity.ok(new ApiResponseDto<>(
                    200,
                    true,
                    "Estadísticas obtenidas correctamente",
                    stats.getResult()
            ));
        } catch (Exception e) {
            e.printStackTrace(); // <-- temporal para ver el error en logs
            return ResponseEntity.status(500).body(new ApiResponseDto<>(
                    500,
                    false,
                    "Error al obtener estadísticas: " + e.getMessage(),
                    null
            ));
        }
    }

}
