package com.sushant.RateLimiter.auth.util;

import com.sushant.RateLimiter.auth.dto.AuthTokenResDTO;
import com.sushant.RateLimiter.auth.entity.User;
import com.sushant.RateLimiter.auth.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
@Data
@RequiredArgsConstructor
public class AuthUtils {
    @Value("${jwt.token.secret-key}")
    private String jwtSecret;

    private final long accessTokenValidityInMillis = 1000*60*60;
    private final long refreshTokenValidityInMillis = 1000*60*60*24*7;
    private final UserService userService;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public AuthTokenResDTO generateToken(User user){
        String access = generateAccessToken(user);
        String refresh = generateRefreshToken(user);
        userService.updateUser(user.getId(),refresh);
        Instant instant = Instant.now();
        return new AuthTokenResDTO(access,refresh,instant.toEpochMilli());
    }

    private String generateAccessToken(User user){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidityInMillis);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("plan", user.getPlanType())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSecretKey())
                .compact();
    }

    private String generateRefreshToken(User user){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidityInMillis);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("plan", user.getPlanType())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSecretKey())
                .compact();
    }

    public Claims parseToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims;
    }
}
