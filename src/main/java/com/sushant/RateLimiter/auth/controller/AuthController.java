package com.sushant.RateLimiter.auth.controller;

//Login | Register

import com.sushant.RateLimiter.auth.dto.AuthRequest;
import com.sushant.RateLimiter.auth.dto.AuthTokenResDTO;
import com.sushant.RateLimiter.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("register/initiate/")
    public ResponseEntity<String> initiateRegister(@RequestBody AuthRequest req){
        authService.initiateRegistration(req);
        return new ResponseEntity<>("OTP sent to email", HttpStatus.OK);
    }

    @PostMapping("register/verify/")
    public ResponseEntity<AuthTokenResDTO> verifyRegister(@RequestBody AuthRequest req){
        AuthTokenResDTO authTokenResDTO = authService.verifyAndRegister(req);
        return ResponseEntity.ok(authTokenResDTO);
    }

    @PostMapping("login/initiate/")
    public ResponseEntity<?> initiateLogin(@RequestBody AuthRequest req) {
        authService.initiateLogin(req);
        return ResponseEntity.ok("OTP sent to email");
    }

    @PostMapping("login/verify/")
    public ResponseEntity<AuthTokenResDTO> verifyLogin(@RequestBody AuthRequest req) {
        AuthTokenResDTO token = authService.verifyAndLogin(req);
        return ResponseEntity.ok(token);
    }
}
