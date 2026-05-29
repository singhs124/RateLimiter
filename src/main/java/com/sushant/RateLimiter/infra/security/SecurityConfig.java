package com.sushant.RateLimiter.infra.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
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

//    private final RateLimitFilter rateLimitFilter;
    private final RateLimiterApiKeyFilter rateLimiterApiKeyFilter;

    @Bean
    @Order(1)
    public SecurityFilterChain apiKeyFilterChain(HttpSecurity http){
        http
                .securityMatcher("/api/v1/ratelimit/**")
                .csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth.anyRequest().authenticated())
                .addFilterBefore(rateLimiterApiKeyFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    
    @Bean
    public SecurityFilterChain jwtFilterChain(HttpSecurity http){
        http
                .securityMatcher("/api/v1/app/**")
                .csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth.anyRequest().permitAll());
        return http.build();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http){
//        http
//                .csrf(csrf->csrf.disable())
//                .authorizeHttpRequests(auth->auth.anyRequest().permitAll())
//                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
}
