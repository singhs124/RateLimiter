package com.sushant.RateLimiter.application.auth.exception;

public class InvalidOTPException extends RuntimeException {
    public InvalidOTPException(String message) {
        super(message);
    }
}
