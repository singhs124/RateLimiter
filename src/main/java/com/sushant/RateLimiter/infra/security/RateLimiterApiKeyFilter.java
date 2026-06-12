package com.sushant.RateLimiter.infra.security;

import com.sushant.RateLimiter.auth.dto.ApiKeyDTO;
import com.sushant.RateLimiter.auth.provider.ApiKeyValidator;
import com.sushant.RateLimiter.infra.cache.ApiKeyLookupService;
import com.sushant.RateLimiter.infra.dto.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Data
@Component
@Slf4j
public class RateLimiterApiKeyFilter extends OncePerRequestFilter {

    private final ApiKeyValidator apiKeyValidator;
    private final ApiKeyLookupService apiKeyLookupService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Only apply this filter to ratelimit API paths
        return !request.getRequestURI().startsWith("/api/v1/ratelimit/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String apiKey = request.getHeader("X-API-KEY");
        if (apiKey == null)
            return;
        Object obj = apiKeyValidator.isStructureValid(apiKey);
        if (obj instanceof Boolean) {
            log.debug("Invalid Api Key");
            return;
        }
        String uuid = (String) obj;
        ApiKeyDTO data = apiKeyLookupService.find(uuid);
        if (data == null) {
            log.debug("Api Key is not Found");
            return;
        }
        UserPrincipal userPrincipal = new UserPrincipal(uuid, data.getRatePlan());

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        // Tell Spring Security this request is authenticated
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(userPrincipal, apiKey, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

}
