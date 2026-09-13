package com.example.demo.config;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey signingKey;

    // The key comes from the JWT_SECRET environment variable. Startup fails if it
    // is missing or too weak, rather than silently signing with a bad key.
    public JwtService(@Value("${jwt.secret:}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET is not set: refusing to start without a signing key");
        }
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (RuntimeException e) {
            throw new IllegalStateException("JWT_SECRET must be Base64-encoded");
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 256 bits (32 bytes once decoded)");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // =========================
    // Extraction du username
    // =========================

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // =========================
    // Extraction des rôles
    // =========================

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);

        return claims.get("roles", List.class);
    }

    // =========================
    // Validation du token
    // =========================

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // =========================
    // Vérification expiration
    // =========================

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // =========================
    // Extraction d'un claim
    // =========================

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    // =========================
    // Extraction de tous les claims
    // =========================

    private Claims extractAllClaims(String token) {

        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // =========================
    // Clé de signature
    // =========================

    private SecretKey getSigningKey() {

        return signingKey;
    }
}