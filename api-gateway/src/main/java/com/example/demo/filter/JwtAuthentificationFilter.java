package com.example.demo.filter;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.example.demo.config.JwtService;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthentificationFilter implements WebFilter {

    private final JwtService jwtService;

    public JwtAuthentificationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            WebFilterChain chain) {

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        // Pas de JWT
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String jwt = authHeader.substring(7);

        try {

            // Vérification du JWT
            if (jwtService.isTokenValid(jwt)) {

                String userEmail = jwtService.extractUsername(jwt);

                List<String> roles = jwtService.extractRoles(jwt);

                List<SimpleGrantedAuthority> authorities = roles
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userEmail,
                                null,
                                authorities
                        );

                return chain
                        .filter(exchange)
                        .contextWrite(
                                ReactiveSecurityContextHolder
                                        .withAuthentication(authentication)
                        );
            }

        } catch (Exception e) {

            System.out.println(
                    "❌ Token invalide ou expiré : "
                    + e.getMessage()
            );

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        // JWT présent mais invalide
        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }
}