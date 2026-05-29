package com.sushant.RateLimiter.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyDTO implements Serializable {
    private String uuid;
    private String keyHashed;
    private String ratePlan;
    private Boolean isRevoked;
}

