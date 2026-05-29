package com.sushant.RateLimiter.application.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Component
public class AuthUtils {
    @Value("${jwt.token.secret-key}")
    private String jwtSecret;

    private final long accessTokenValidityInMillis = 1000*60*60;
    private final long refreshTokenValidityInMillis = 1000*60*60*24*7;

    public AuthUtils() throws NoSuchAlgorithmException {
    }

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String userId){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidityInMillis);

        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSecretKey())
                .compact();
    }

    public String generateRefreshToken(String userId){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidityInMillis);

        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSecretKey())
                .compact();
    }

    public String parseToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}
