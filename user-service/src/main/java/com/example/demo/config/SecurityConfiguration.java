package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    

    private final JwtAuthentificationFilter jwtAuthentificationFilter;

    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        
        // No CORS here: browsers only talk to the api-gateway, which handles it.
        http
            .csrf(csrf-> csrf.disable())
            .authorizeHttpRequests(
                request -> request

                    .requestMatchers("/api/auth/admin/**").hasRole("Admin")

                    .requestMatchers("/api/auth/**").permitAll()
                    
                    .requestMatchers("/api/customer/download/**").permitAll()

                    .requestMatchers("/api/customer/**").hasAnyRole("Admin","Customer")

                    .requestMatchers("/api/admin/**").hasRole("Admin")




                    .anyRequest().authenticated()
                  
            )
            .addFilterBefore(jwtAuthentificationFilter, UsernamePasswordAuthenticationFilter.class)
            
            .authenticationProvider(authenticationProvider)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        return http.build();
    }

}
