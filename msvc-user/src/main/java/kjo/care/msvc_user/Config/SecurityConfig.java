package kjo.care.msvc_user.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll() // Permitir acceso a todas las rutas
                )
                .csrf(csrf -> csrf.disable()) // Desactivar CSRF si es necesario
                .formLogin(form -> form.disable()) // Deshabilitar el login
                .httpBasic(httpBasic -> httpBasic.disable()); // Deshabilitar autenticación básica

        return http.build();
    }

}
