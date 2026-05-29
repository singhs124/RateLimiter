package com.sushant.RateLimiter.application.auth.controller;

//Login | Register

import com.sushant.RateLimiter.application.auth.dto.AuthRequest;
import com.sushant.RateLimiter.application.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/app/auth/")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody AuthRequest req) {
        authService.registerUser(req.getEmail());
        return new ResponseEntity<>("Proceed to login", HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody AuthRequest req) {
        boolean exists = authService.checkEmailExists(req.getEmail());
        if (exists) {
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("generateApiKey")
    public ResponseEntity<String> generateAPiKey(@RequestBody AuthRequest req) {
        String apiKey = authService.generateAPIkey(req.getEmail());
        return new ResponseEntity<>(apiKey, HttpStatus.OK);
    }
}
