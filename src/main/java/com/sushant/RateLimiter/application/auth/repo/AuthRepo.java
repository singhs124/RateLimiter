package com.sushant.RateLimiter.application.auth.repo;

import com.sushant.RateLimiter.application.auth.entity.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepo extends JpaRepository<UserOtp,Long> {
    UserOtp findByUserIdentifier(String userIdentifier);
}
