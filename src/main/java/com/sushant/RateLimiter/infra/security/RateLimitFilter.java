package com.sushant.RateLimiter.infra.security;

import com.sushant.RateLimiter.ratelimit.service.RateLimitingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Deprecated
@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimitingService rateLimitingService;

    @Override
    //Todo: refactor getWriter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String identifier = getIdentifier(request);
//        if(!rateLimitingService.allowRequest(identifier,1)){
//            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
//            response.setContentType("application/json");
//            response.getWriter().write(
//                    "{}"
//            );
//            return;
//        }
//        long remainingTokens = rateLimitingService.getAvailableTokens(identifier);
//        response.setHeader("X-RateLimit-Remaining", String.valueOf(remainingTokens));
        filterChain.doFilter(request,response);
    }

    private String getIdentifier(HttpServletRequest req){
        String xForwardedFor = req.getHeader("X-Forwarded-For");
        if(xForwardedFor != null && !xForwardedFor.isEmpty()){
            log.info("Getting 'X-Forwarded-For' Header");
            return xForwardedFor.split(",")[0].trim();
        }
        String ipAdd = req.getRemoteAddr();
        log.debug("Getting Request from " + ipAdd);
        return ipAdd;
    }
}
