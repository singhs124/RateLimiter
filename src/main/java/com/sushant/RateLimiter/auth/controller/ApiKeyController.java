package com.sushant.RateLimiter.auth.controller;

import com.sushant.RateLimiter.auth.dto.AuthRequest;
import com.sushant.RateLimiter.auth.service.ApiKeyService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/apikey/")
@Data
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping("generateApiKey")
    public ResponseEntity<String> generateAPiKey(@RequestBody AuthRequest req) {
        String apiKey = apiKeyService.generateAPIkey(req.getEmail());
        return new ResponseEntity<>(apiKey, HttpStatus.OK);
    }
}
