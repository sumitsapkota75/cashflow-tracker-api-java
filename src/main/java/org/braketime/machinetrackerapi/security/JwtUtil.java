package org.braketime.machinetrackerapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    // Load secret and expiration from application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms:3600000}") // default 1 hour
    private long jwtExpirationMs;

    // Ensure secret is at least 32 bytes for HS256
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

        // HS256 requires at least 256 bits (32 bytes)
        if (keyBytes.length < 32) {
            log.warn("JWT Secret is too short for HS256. Using a padded version.");
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
            return Keys.hmacShaKeyFor(paddedKey);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate JWT token for a given username/email and role
    public String generateToken(String username, String role) {
        log.info("Generating JWT for user: {}, role: {}", username, role);

        return Jwts.builder()
                .subject(username)
                .claim("role", role) // include role as claim
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // Extract username/email from JWT
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract role from JWT
    public String extractRole(String token) {
        return (String) extractClaims(token).get("role");
    }

    // Validate JWT token
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            boolean expired = claims.getExpiration().before(new Date());
            if (expired) log.warn("JWT token is expired: {}", token);
            return !expired;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", token, e);
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Now correctly resolves SecretKey
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
