package com.sushant.RateLimiter.infra.security;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Data
public class SecurityConfig {

    private final RateLimiterApiKeyFilter rateLimiterApiKeyFilter;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    @Order(1)
    public SecurityFilterChain publicFilterChain(HttpSecurity http){
        http
                .securityMatcher("/api/v1/auth/**")
                .csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain appFilterChain(HttpSecurity http){
        http
                .securityMatcher("/api/v1/app/**")
                .csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth.anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain rateLimitFilterChain(HttpSecurity http){
        http
                .securityMatcher("/api/v1/ratelimit/**")
                .csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth.anyRequest().authenticated())
                .addFilterBefore(rateLimiterApiKeyFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
