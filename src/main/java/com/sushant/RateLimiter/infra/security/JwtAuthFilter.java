package com.sushant.RateLimiter.infra.security;

import com.sushant.RateLimiter.auth.util.AuthUtils;
import com.sushant.RateLimiter.infra.dto.JwtUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthUtils authUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("Incoming request to JWT Auth Filter: " + request.getRequestURI());
        String requestHeader = request.getHeader("Authorization");
        if(requestHeader == null || !requestHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        String token = requestHeader.split("Bearer ")[1];
        try{
            Claims claims = authUtils.parseToken(token);
            String userId = claims.getSubject();
            String email = claims.get("email", String.class);
            String plan = claims.get("plan", String.class);

            JwtUserPrincipal principal = new JwtUserPrincipal(userId, email, plan);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    principal,null,null
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request,response);
        } catch (ExpiredJwtException ex){
            log.error("Expired token in JWTAuthFilter" + ex);
            sendError(response, "Token Expired");
        } catch (JwtException ex){
            log.error("Exception At JWTAuthFilter" + ex);
            sendError(response, "Invalid Token");
        }
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
