package com.analytics.client;

import com.analytics.DTOs.DailyActivitySummaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyActivityClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${microservices.daily-activity.url}")
    private String dailyActivityServiceUrl;

    public Mono<DailyActivitySummaryDTO> getDailyActivitySummary(String userId) {
        return webClientBuilder
                .baseUrl(dailyActivityServiceUrl)
                .build()
                .get()
                .uri("/assignments/summary/daily/{userId}", userId)
                .retrieve()
                .bodyToMono(DailyActivitySummaryDTO.class)
                .doOnError(error -> log.error("Error al obtener el resumen de actividad diaria para el usuario {}: {}", userId, error.getMessage()))
                .onErrorResume(error -> Mono.just(new DailyActivitySummaryDTO(0, 0)));
    }

}
