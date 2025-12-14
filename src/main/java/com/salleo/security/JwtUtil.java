package com.salleo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expirationSeconds}")
    private long expirationSeconds;

    private Key key;

    @PostConstruct
    public void init() {
        try {
            byte[] secretBytes = secret == null ? new byte[0] : secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            if (secretBytes.length < 32) {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                secretBytes = md.digest(secretBytes);
            }
            key = Keys.hmacShaKeyFor(secretBytes);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize JWT key", e);
        }
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationSeconds * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return jws.getBody();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
