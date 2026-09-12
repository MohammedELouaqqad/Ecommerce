package com.example.demo.config;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "aBY638xOVWcOF/0duj8ox+Jkum2VRgjz2yiXsp96qf8=";

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

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}