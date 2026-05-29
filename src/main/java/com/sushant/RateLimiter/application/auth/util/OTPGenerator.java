package com.sushant.RateLimiter.application.auth.util;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;


@Slf4j
public class OTPGenerator {
    private final static SecureRandom random = new SecureRandom();
    public static String generateOtp(){
        log.trace("Generating OTP");
        int otp = 100000 + random.nextInt(900000);
        log.debug("OTP is: {}", otp);
        return String.valueOf(otp);
    }
}
