package com.analytics.client;

import com.analytics.DTOs.BlogCountDto;
import com.analytics.DTOs.DailyBlogCountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlogClient {
    private final WebClient.Builder webClientBuilder;
    @Value("${microservices.blog.url}")
    private String blogServiceUrl;

    public Mono<Long> getTotalBlogs() {
        return webClientBuilder.build()
                .get()
                .uri(blogServiceUrl + "/blogs/count")
                .retrieve()
                .bodyToMono(BlogCountDto.class)
                .map(BlogCountDto::count)
                .doOnError(error -> {
                    log.error("Error al obtener el total de blogs: {}", error.getMessage());
                }).onErrorResume(error -> Mono.just(0L));
    }

    public Mono<Long> getPreviousMonthBlogs() {
        return webClientBuilder.build()
                .get()
                .uri(blogServiceUrl + "/blogs/count/previous-month")
                .retrieve()
                .bodyToMono(BlogCountDto.class)
                .map(BlogCountDto::count)
                .doOnError(error -> {
                    log.error("Error al obtener blogs del mes anterior: {}", error.getMessage());
                })
                .onErrorResume(error -> Mono.just(0L));
    }

    public Mono<List<Object[]>> getBlogCountsByDayBetweenDates(LocalDate startDate, LocalDate endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedStart = startDate.format(formatter);
        String formattedEnd = endDate.format(formatter);

        String url = String.format("http://msvc-blog:9004/blogs/countByDay?state=PUBLICADO&startDate=%s&endDate=%s",
                formattedStart, formattedEnd);

        log.info("Llamando al servicio de blogs: {}", url);

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Map.class)
                .map(map -> new Object[] { mapDateString(map.get("date")), mapCount(map.get("count")) })
                .doOnNext(result -> log.debug("Objeto recibido: {}", Arrays.toString(result)))
                .collectList()
                .doOnSuccess(list -> log.info("Recibidos {} registros de conteo diario", list.size()))
                .doOnError(error -> {
                    log.error("Error al obtener conteos diarios de blogs: {}", error.getMessage());
                })
                .onErrorResume(error -> {
                    log.warn("Generando datos vacíos para fechas de {} a {}", startDate, endDate);
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
