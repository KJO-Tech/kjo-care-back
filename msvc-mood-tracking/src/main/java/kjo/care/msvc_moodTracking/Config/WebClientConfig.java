package kjo.care.msvc_moodTracking.Config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    WebClient.Builder webClient() {
        return WebClient.builder()
                .filter(authHeaderFilter());
    }

    private ExchangeFilterFunction authHeaderFilter() {
        return (request, next) -> {
            try {
                JwtAuthenticationToken authentication =
                        (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

                if (authentication != null && authentication.getToken() != null) {
                    String token = authentication.getToken().getTokenValue();
                    ClientRequest filteredRequest = ClientRequest.from(request)
                            .header("Authorization", "Bearer " + token)
                            .build();
                    return next.exchange(filteredRequest);
                }
            } catch (
                    Exception e) {
                System.err.println("Error extracting token: " + e.getMessage());
            }

            return next.exchange(request);
        };
    }
}