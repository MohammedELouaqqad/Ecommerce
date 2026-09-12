package com.example.demo.config;


import com.example.demo.filter.JwtAuthentificationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

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
}