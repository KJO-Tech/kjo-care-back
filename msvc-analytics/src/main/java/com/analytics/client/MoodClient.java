package com.analytics.client;

import com.analytics.DTOs.MoodCountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MoodClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${microservices.mood-tracking.url}")
    private String moodServiceUrl;

    public Mono<Long> getMoodLogDays(String userId) {
        return webClientBuilder
                .baseUrl(moodServiceUrl)
                .build()
                .get()
                .uri("/user-mood/mood-log-days/{userId}", userId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new RuntimeException("Error al obtener días de registro de ánimo: " + errorBody)
                                ))
                )
                .bodyToMono(Long.class);
    }

    public Mono<Long> getTotalMoods() {
        // Método existente
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
        // Método existente
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
    
    public Mono<List<Object[]>> getMoodUserCountsByDayLastMonth() {
        String url = moodServiceUrl + "/user-mood/count-by-day";
        
        log.info("Llamando al servicio de estados de ánimo: {}", url);
        
        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Map.class)
                .map(map -> new Object[]{mapDateString(map.get("date")), mapCount(map.get("count"))})
                .doOnNext(result -> log.debug("Objeto recibido: {}", Arrays.toString(result)))
                .collectList()
                .doOnSuccess(list -> log.info("Recibidos {} registros de conteo diario de usuarios", list.size()))
                .doOnError(error -> {
                    log.error("Error al obtener conteos diarios de usuarios con estados de ánimo: {}", error.getMessage());
                })
                .onErrorResume(error -> {
                    log.warn("Generando datos vacíos para conteos diarios de usuarios");
                    return Mono.just(List.of());
                });
    }
    
    private Object mapDateString(Object dateObj) {
        try {
            if (dateObj instanceof String) {
                return java.sql.Date.valueOf((String) dateObj);
            }
            return dateObj;
        } catch (Exception e) {
            log.error("Error al convertir fecha: {}", e.getMessage());
            return new java.sql.Date(System.currentTimeMillis());
        }
    }
    
    private Long mapCount(Object countObj) {
        try {
            if (countObj instanceof Number) {
                return ((Number) countObj).longValue();
            } else if (countObj instanceof String) {
                return Long.parseLong((String) countObj);
            }
            return 0L;
        } catch (Exception e) {
            log.error("Error al convertir conteo: {}", e.getMessage());
            return 0L;
        }
    }
}