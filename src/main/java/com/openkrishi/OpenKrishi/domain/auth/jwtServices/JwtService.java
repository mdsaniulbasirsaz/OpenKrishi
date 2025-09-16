package com.openkrishi.OpenKrishi.domain.auth.jwtServices;


import com.openkrishi.OpenKrishi.domain.auth.dtos.AuthResponseDto;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final Key key;
    private final long jwtExpirationInMs;

    public JwtService() {
        // Load secrets from environment variables (production)
        String secret = System.getenv("JWT_SECRET");
        String expirationStr = System.getenv("JWT_EXPIRATION");


        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET is not set in .env file");
        }
        if (expirationStr == null || expirationStr.isEmpty()) {
            throw new IllegalStateException("JWT_EXPIRATION is not set in .env file");
        }

        this.jwtExpirationInMs = Long.parseLong(expirationStr);
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email, String fullName, UUID userId, String role ) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("fullName", fullName)
                .claim("userId", userId.toString())
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    public String getRoleFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }


    public String getEmailFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public String getFullNameFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("fullName", String.class);
    }

    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        String userIdStr = claims.get("userId", String.class);
        if (userIdStr == null || userIdStr.isEmpty()) {
            throw new IllegalArgumentException("User ID not present in token");
        }
        return UUID.fromString(userIdStr);
    }


    public AuthResponseDto buildAuthResponse(String email, String fullName, UUID userId, String role) {
        String token = generateToken(email, fullName, userId, role);
        return new AuthResponseDto(token, fullName, email, userId.toString(), role);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
