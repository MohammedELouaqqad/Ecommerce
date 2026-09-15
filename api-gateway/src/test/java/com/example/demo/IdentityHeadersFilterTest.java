package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import com.example.demo.config.JwtService;
import com.example.demo.filter.JwtAuthentificationFilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

/**
 * Les services en aval font confiance aux en-têtes X-User-* : si un client
 * pouvait les envoyer lui-même, il commanderait au nom de n'importe qui. C'est
 * le seul endroit qui garantit cette frontière.
 */
class IdentityHeadersFilterTest {

    private static final String SECRET = "dGVzdC1zZWNyZXQtcG91ci1sZXMtdGVzdHMtMjU2LWJpdHMhIQ==";
    private static final String OTHER_SECRET = "YXV0cmUtY2xlLXF1aS1uZS1kb2l0LXBhcy1tYXJjaGVyLTEyMw==";

    private final JwtService jwtService = new JwtService(SECRET);
    private final JwtAuthentificationFilter filter = new JwtAuthentificationFilter(jwtService);

    private static String token(String secret, String email, Long userId) {
        return Jwts.builder()
                .subject(email)
                .claim("roles", List.of("ROLE_Customer"))
                .claim("userId", userId)
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)), Jwts.SIG.HS256)
                .compact();
    }

    /** Exécute le filtre et renvoie la requête telle que la verront les services, ou null si elle est bloquée. */
    private ServerHttpRequest forward(MockServerHttpRequest.BaseBuilder<?> request, MockServerWebExchange[] out) {
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        out[0] = exchange;
        AtomicReference<ServerHttpRequest> forwarded = new AtomicReference<>();
        WebFilterChain chain = forwardedExchange -> {
            forwarded.set(forwardedExchange.getRequest());
            return Mono.empty();
        };
        filter.filter(exchange, chain).block();
        return forwarded.get();
    }

    @Test
    @DisplayName("Les en-têtes d'identité envoyés par le client sont remplacés par ceux du jeton")
    void clientSuppliedIdentityHeadersAreReplacedByTheTokenOnes() {
        MockServerWebExchange[] exchange = new MockServerWebExchange[1];
        ServerHttpRequest forwarded = forward(MockServerHttpRequest.get("/api/customer/allOrders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(SECRET, "alice@shop.local", 7L))
                .header("X-User-Id", "999")
                .header("X-User-Email", "bob@evil.com")
                .header("X-User-Roles", "ROLE_Admin"), exchange);

        assertThat(forwarded).isNotNull();
        assertThat(forwarded.getHeaders().getFirst("X-User-Id")).isEqualTo("7");
        assertThat(forwarded.getHeaders().getFirst("X-User-Email")).isEqualTo("alice@shop.local");
        assertThat(forwarded.getHeaders().getFirst("X-User-Roles")).isEqualTo("ROLE_Customer");
    }

    @Test
    @DisplayName("Sans jeton, aucun en-tête d'identité n'est transmis, même si le client en envoie")
    void anonymousRequestsCarryNoIdentityHeaders() {
        MockServerWebExchange[] exchange = new MockServerWebExchange[1];
        ServerHttpRequest forwarded = forward(MockServerHttpRequest.get("/api/customer/allProducts")
                .header("X-User-Id", "999")
                .header("X-User-Email", "bob@evil.com")
                .header("X-User-Roles", "ROLE_Admin"), exchange);

        assertThat(forwarded).isNotNull();
        assertThat(forwarded.getHeaders().get("X-User-Id")).isNull();
        assertThat(forwarded.getHeaders().get("X-User-Email")).isNull();
        assertThat(forwarded.getHeaders().get("X-User-Roles")).isNull();
    }

    @Test
    @DisplayName("Un jeton signé avec une autre clé est rejeté et n'atteint aucun service")
    void aTokenSignedWithAnotherKeyIsRejected() {
        MockServerWebExchange[] exchange = new MockServerWebExchange[1];
        ServerHttpRequest forwarded = forward(MockServerHttpRequest.get("/api/customer/allOrders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(OTHER_SECRET, "pirate@evil.com", 999L)), exchange);

        assertThat(forwarded).isNull();
        assertThat(exchange[0].getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
