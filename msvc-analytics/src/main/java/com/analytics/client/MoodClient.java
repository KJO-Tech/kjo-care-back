package com.analytics.client;

import com.analytics.DTOs.MoodCountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class MoodClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${microservices.mood-tracking.url}")
    private String moodServiceUrl;

    public Mono<Long> getTotalMoods() {
        return webClientBuilder.build()
                .get()
                .uri(moodServiceUrl + "/user-mood/count")
                .retrieve()
                .bodyToMono(MoodCountDto.class)
                .map(MoodCountDto::count)
                .doOnError(error -> {
                    log.error("Error al obtener total de estados de ánimo: {}", error.getMessage());
                })
                .onErrorResume(error -> Mono.just(0L));
    }

    public Mono<Long> getPreviousMonthMoods() {
        return webClientBuilder.build()
                .get()
                .uri(moodServiceUrl + "/user-mood/count/previous-month")
                .retrieve()
                .bodyToMono(MoodCountDto.class)
                .map(MoodCountDto::count)
                .doOnError(error -> {
                    log.error("Error al obtener estados de ánimo del mes anterior: {}", error.getMessage());
                })
                .onErrorResume(error -> Mono.just(0L));
    }
}