package com.sushant.RateLimiter.auth.service;


import com.sushant.RateLimiter.auth.dto.AuthRequest;
import com.sushant.RateLimiter.auth.dto.AuthTokenResDTO;
import com.sushant.RateLimiter.auth.entity.*;

import com.sushant.RateLimiter.auth.exception.UserNotFoundException;
import com.sushant.RateLimiter.auth.repo.UserRepository;
import com.sushant.RateLimiter.auth.repo.UserAPIRepository;
import com.sushant.RateLimiter.auth.provider.ApiKeyGenerator;
import com.sushant.RateLimiter.auth.util.*;
import com.sushant.RateLimiter.infra.annotation.ReadOnlyConnection;
import com.sushant.RateLimiter.infra.cache.ApiKeyCacheService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Data
@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AuthUtils authUtil;
    private final UserService userService;
    private final UserAPIRepository userAPIRepository;
    private final ApiKeyGenerator apiKeyGenerator;
    private final ApiKeyCacheService apiKeyCacheService;
    private final OtpService otpService;
//    private final JavaMailSender javaMailSender;

//    @Value("${mail.sender.name}")
//    private String senderName;
//
//    @Value("${mail.sender.email}")
//    private String senderEmail;

    private boolean validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    @ReadOnlyConnection
    public void initiateRegistration(AuthRequest authRequest){
        if(userRepository.existsByEmail(authRequest.getEmail())){
            log.debug("User Already Exists"); //todo: Add exception
            return ;
        }
        otpService.generateAndSend(authRequest);
    }

    public AuthTokenResDTO verifyAndRegister(AuthRequest request){
        otpService.validate(request);

        User user = User.builder()
                .email(request.getEmail())
                .planType(Plans.FREE)
                .build();
        userRepository.save(user);
        return authUtil.generateToken(user);
    }

    @ReadOnlyConnection
    public void initiateLogin(AuthRequest authRequest) {
        if (!userRepository.existsByEmail(authRequest.getEmail())) {
            throw new UserNotFoundException("User Not Found");
        }
        otpService.generateAndSend(authRequest);
    }

    @ReadOnlyConnection
    public AuthTokenResDTO verifyAndLogin(AuthRequest authRequest) {
        otpService.validate(authRequest);
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(()->new UserNotFoundException("User Not Found"));
        return authUtil.generateToken(user);
    }
    // Todo: Revoke APi Key
}
