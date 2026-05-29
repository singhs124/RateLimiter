package com.sushant.RateLimiter.common.dto;

import org.springframework.http.HttpStatus;

public record ErrorMap(String message, HttpStatus httpStatus) {
}
