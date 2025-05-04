package com.analytics.client;
import com.analytics.DTOs.HealthCenterCountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Component
@RequiredArgsConstructor
@Slf4j
public class EmergencyClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${microservices.emergency.url}")
    private String emergencyServiceUrl;

    public Mono<Long> getTotalHealthCenters() {
        return webClientBuilder.build()
                .get()
                .uri(emergencyServiceUrl + "/centers/health-centers/count")
                .retrieve()
                .bodyToMono(HealthCenterCountDto.class)
                .map(HealthCenterCountDto::count)
                .map(Long::valueOf)
                .doOnError(error -> {
                    log.error("Error al obtener total de centros de salud: {}", error.getMessage());
                })
                .onErrorResume(error -> Mono.just(0L));
    }

    public Mono<Long> getActiveHealthCenters() {
        return webClientBuilder.build()
                .get()
                .uri(emergencyServiceUrl + "/resources/health-centers/count/active")
                .retrieve()
                .bodyToMono(HealthCenterCountDto.class)
                .map(HealthCenterCountDto::count)
                .map(Long::valueOf)
                .doOnError(error -> {
                    log.error("Error al obtener total de centros de salud activos: {}", error.getMessage());
                })
                .onErrorResume(error -> Mono.just(0L));
    }

    public Mono<Long> getPreviousMonthHealthCenters() {
        return webClientBuilder.build()
                .get()
                .uri(emergencyServiceUrl + "/resources/health-centers/count/previous-month")
                .retrieve()
                .bodyToMono(HealthCenterCountDto.class)
                .map(HealthCenterCountDto::count)
                .map(Long::valueOf)
                .doOnError(error -> {
                    log.error("Error al obtener centros de salud del mes anterior: {}", error.getMessage());
                })
                .onErrorResume(error -> Mono.just(0L));
    }
}