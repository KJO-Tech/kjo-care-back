package com.analytics.client;

import com.analytics.DTOs.UserCountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${microservices.auth.url}")
    private String authServiceUrl;

    public Mono<Long> getTotalUsers() {
        return getTotalUsersByPeriod(3);
    }

    public Mono<Long> getTotalUsersByPeriod(int months) {
        return webClientBuilder
                .baseUrl(authServiceUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/count")
                        .queryParam("months", months)
                        .build())
                .retrieve()
                .bodyToMono(UserCountDto.class)
                .map(UserCountDto::count)
                .doOnError(error -> {
                    log.error("Error al obtener total de usuarios para {} meses: {}", months, error.getMessage());
                })
                .onErrorResume(error -> Mono.just(0L));
    }

    public Mono<Long> getAllUsers() {
        return webClientBuilder.build()
                .get()
                .uri(authServiceUrl + "/users/count/all")
                .retrieve()
                .bodyToMono(UserCountDto.class)
                .map(UserCountDto::count)
                .doOnError(error -> {
                    log.error("Error al obtener total de todos los usuarios: {}", error.getMessage());
                })
                .onErrorResume(error -> Mono.just(0L));
    }

    public Mono<Long> getPreviousMonthUsers() {
        return webClientBuilder.build()
                .get()
                .uri(authServiceUrl + "/users/count/previous-month")
                .retrieve()
                .bodyToMono(UserCountDto.class)
                .map(UserCountDto::count)
                .doOnError(error -> {
                    log.error("Error al obtener usuarios del mes anterior: {}", error.getMessage());
                })
                .onErrorResume(error -> Mono.just(0L));
    }
}