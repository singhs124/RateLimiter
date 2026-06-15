package com.sushant.RateLimiter.auth.controller;

import com.sushant.RateLimiter.auth.dto.AuthRequest;
import com.sushant.RateLimiter.auth.service.ApiKeyService;
import com.sushant.RateLimiter.infra.dto.JwtUserPrincipal;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/app/apikey/")
@Data
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping("generateApiKey")
    public ResponseEntity<String> generateAPiKey(@AuthenticationPrincipal JwtUserPrincipal principal) {
        String apiKey = apiKeyService.generateAPIkey(principal.email());
        return new ResponseEntity<>(apiKey, HttpStatus.OK);
    }
}
