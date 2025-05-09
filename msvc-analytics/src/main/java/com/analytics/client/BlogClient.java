package com.analytics.client;

import com.analytics.DTOs.BlogCountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

}
