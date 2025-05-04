package com.analytics.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(authHeaderFilter());
    }

    private ExchangeFilterFunction authHeaderFilter() {
        return (request, next) -> {
            return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    if (securityContext.getAuthentication() instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken authentication = (JwtAuthenticationToken) securityContext.getAuthentication();
                        String token = authentication.getToken().getTokenValue();
                        System.out.println("Propagando token del usuario a la solicitud");
                        return ClientRequest.from(request)
                                .header("Authorization", "Bearer " + token)
                                .build();
                    }
                    return request;
                })
                .defaultIfEmpty(request)
                .flatMap(next::exchange);
        };
    }
}