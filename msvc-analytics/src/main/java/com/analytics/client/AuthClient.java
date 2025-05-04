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

    @Value("${microservices.users.url}")
    private String authServiceUrl;

    public Mono<Long> getTotalUsers() {
        return webClientBuilder.build()
                .get()
                .uri(authServiceUrl + "/users/count")
                .retrieve()
                .bodyToMono(UserCountDto.class)
                .map(UserCountDto::count)
                .doOnError(error -> {
                    log.error("Error al obtener total de usuarios: {}", error.getMessage());
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