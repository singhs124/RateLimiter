package com.sushant.RateLimiter.ratelimit.controller;


import com.sushant.RateLimiter.infra.dto.UserPrincipal;
import com.sushant.RateLimiter.ratelimit.dto.RateLimitReq;
import com.sushant.RateLimiter.ratelimit.dto.RateLimitResponse;
import com.sushant.RateLimiter.ratelimit.dto.RateLimitResult;
import com.sushant.RateLimiter.ratelimit.service.RateLimitFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/ratelimit")
@RequiredArgsConstructor
public class RateLimitController {
    private final RateLimitFacade rateLimitFacade;

    @PostMapping("/checkLimit")
    public ResponseEntity<RateLimitResponse> checkLimit(Authentication auth, @RequestBody RateLimitReq body){
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String uuid = principal.uuid();
        String ratePlan = principal.ratePlan();

        RateLimitResult result = rateLimitFacade.evaluate(uuid,ratePlan,body.algo());

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RateLimit-Remaining", String.valueOf(result.remaining()));

        HttpStatus status = result.allowed() ? HttpStatus.OK : HttpStatus.TOO_MANY_REQUESTS;

        return ResponseEntity.status(status)
                .headers(headers)
                .body(new RateLimitResponse(result.allowed()));
    }
}
