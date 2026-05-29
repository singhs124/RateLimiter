package com.sushant.RateLimiter.ratelimit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/rateLimit")
public class TestController {
    @GetMapping("/test")
    public String testController(){
        return "Hello - Passed!";
    }
}
