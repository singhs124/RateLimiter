package com.sushant.RateLimiter.application.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    String email;
    String otp;
}
