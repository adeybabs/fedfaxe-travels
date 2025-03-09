package com.project.fedfaxe.utils;

import com.project.fedfaxe.config.JwtConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationTime;

    @Autowired
    public JwtUtil(JwtConfig jwtConfig) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtConfig.getSecretKey()));
       // this.key = Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
        this.expirationTime = jwtConfig.getExpirationTime();
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
