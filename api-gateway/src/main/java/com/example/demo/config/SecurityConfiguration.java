package com.example.demo.config;


import java.util.List;

import com.example.demo.filter.JwtAuthentificationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    private final JwtAuthentificationFilter jwtAuthentificationFilter;

    public SecurityConfiguration(
            JwtAuthentificationFilter jwtAuthentificationFilter) {
        this.jwtAuthentificationFilter = jwtAuthentificationFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges

                .pathMatchers("/api/auth/admin/**")
                    .hasRole("Admin")

                .pathMatchers("/api/auth/**")
                    .permitAll()

                .pathMatchers("/api/customer/download/**")
                    .permitAll()

                .pathMatchers("/api/customer/**")
                    .hasAnyRole("Admin", "Customer")

                .pathMatchers("/api/admin/**")
                    .hasRole("Admin")

                .anyExchange()
                    .authenticated()
            )
            .addFilterAt(
                jwtAuthentificationFilter,
                SecurityWebFiltersOrder.AUTHENTICATION
            )
            .securityContextRepository(
                NoOpServerSecurityContextRepository.getInstance()
            );

        return http.build();
    }

    // The browser only talks to the gateway, so CORS is handled here. With this bean,
    // .cors() answers preflight requests itself, before authentication: a preflight
    // never carries the token, so it would otherwise be rejected with 401.
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${cors.allowed-origins}") List<String> allowedOrigins) {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // The JWT travels in a header, not in a cookie.
        configuration.setAllowCredentials(false);
        // Lets the browser reuse a preflight answer for an hour.
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}