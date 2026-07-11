package com.sushant.RateLimiter.auth.service;


import com.sushant.RateLimiter.auth.dto.AuthRequest;
import com.sushant.RateLimiter.auth.dto.AuthTokenResDTO;
import com.sushant.RateLimiter.auth.entity.*;

import com.sushant.RateLimiter.auth.exception.UserNotFoundException;
import com.sushant.RateLimiter.auth.repo.UserRepository;
import com.sushant.RateLimiter.auth.repo.UserAPIRepository;
import com.sushant.RateLimiter.auth.provider.ApiKeyGenerator;
import com.sushant.RateLimiter.auth.util.*;
import com.sushant.RateLimiter.infra.cache.ApiKeyCacheService;
import com.sushant.RateLimiter.infra.config.ShardRouter;
import com.sushant.RateLimiter.infra.dsRouting.ShardContextHolder;
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

    public void initiateRegistration(AuthRequest authRequest){
        ShardContextHolder.set(ShardRouter.resolveShardId(authRequest.getEmail()));
        try{
            if(userRepository.existsByEmail(authRequest.getEmail())){
                log.debug("User Already Exists"); //todo: Add exception
                return ;
            }
            otpService.generateAndSend(authRequest);
        } finally {
            ShardContextHolder.clear();
        }
    }

    public AuthTokenResDTO verifyAndRegister(AuthRequest request){
        ShardContextHolder.set(ShardRouter.resolveShardId(request.getEmail()));
        try {
            otpService.validate(request);

            User user = User.builder()
                    .email(request.getEmail())
                    .planType(Plans.FREE)
                    .build();
            userRepository.save(user);
            return authUtil.generateToken(user);
        } finally {
            ShardContextHolder.clear();
        }
    }

    public void initiateLogin(AuthRequest authRequest) {
        ShardContextHolder.set(ShardRouter.resolveShardId(authRequest.getEmail()));
        try{
            validateUser(authRequest);
            otpService.generateAndSend(authRequest);
        } finally {
            ShardContextHolder.clear();
        }
    }

    public AuthTokenResDTO verifyAndLogin(AuthRequest authRequest) {
        ShardContextHolder.set(ShardRouter.resolveShardId(authRequest.getEmail()));
        try{
            otpService.validate(authRequest);
            User user = validateUser(authRequest);
            return authUtil.generateToken(user);
        } finally {
            ShardContextHolder.clear();
        }
    }

    private User validateUser(AuthRequest authRequest){
        return userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(()->new UserNotFoundException("User Not Found"));
    }
    // Todo: Revoke APi Key
}
