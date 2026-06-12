package com.sushant.RateLimiter.auth.repo;

import com.sushant.RateLimiter.auth.entity.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepo extends JpaRepository<UserOtp,Long> {
    UserOtp findByUserIdentifier(String userIdentifier);
}
