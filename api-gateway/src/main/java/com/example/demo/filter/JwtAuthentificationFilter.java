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

    // Identity headers read by the downstream services. Only the gateway sets them,
    // from the verified token: the services trust them instead of the request body.
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String USER_ROLES_HEADER = "X-User-Roles";
    private static final List<String> IDENTITY_HEADERS =
            List.of(USER_ID_HEADER, USER_EMAIL_HEADER, USER_ROLES_HEADER);

    private final JwtService jwtService;

    public JwtAuthentificationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            WebFilterChain chain) {

        // A client could send these headers itself to pass as someone else:
        // they are dropped from every request, whatever happens next.
        ServerWebExchange stripped = exchange.mutate()
                .request(request -> request.headers(headers -> IDENTITY_HEADERS.forEach(headers::remove)))
                .build();

        String authHeader = stripped.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        // Pas de JWT
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(stripped);
        }

        String jwt = authHeader.substring(7);

        try {

            // Vérification du JWT
            if (jwtService.isTokenValid(jwt)) {

                String userEmail = jwtService.extractUsername(jwt);

                List<String> roles = jwtService.extractRoles(jwt);

                String userId = jwtService.extractUserId(jwt);

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

                // Tell the downstream services who is calling.
                ServerWebExchange withIdentity = stripped.mutate()
                        .request(request -> request.headers(headers -> {
                            if (userId != null) {
                                headers.set(USER_ID_HEADER, userId);
                            }
                            headers.set(USER_EMAIL_HEADER, userEmail);
                            headers.set(USER_ROLES_HEADER, String.join(",", roles));
                        }))
                        .build();

                return chain
                        .filter(withIdentity)
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

            stripped.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return stripped.getResponse().setComplete();
        }

        // JWT présent mais invalide
        stripped.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        return stripped.getResponse().setComplete();
    }
}
