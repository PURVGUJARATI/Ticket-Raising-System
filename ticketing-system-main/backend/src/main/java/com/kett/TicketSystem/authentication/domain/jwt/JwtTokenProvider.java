package com.kett.TicketSystem.authentication.domain.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

@Component
public class JwtTokenProvider {

    private static final Logger logger = Logger.getLogger(JwtTokenProvider.class.getName());

    private final Key jwtKey;

    public JwtTokenProvider() {
        String secretKey = "your_super_secret_key_that_should_be_very_long";  // Replace with actual long key
        byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes());
        this.jwtKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email) {
        Instant now = Instant.now();
        Instant expiration = now.plus(1, ChronoUnit.DAYS);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(jwtKey)
                .compact();
    }

    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return generateToken(user.getUsername());
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            logger.warning("Invalid JWT Token: " + e.getMessage());
            return false;
        }
    }
}
