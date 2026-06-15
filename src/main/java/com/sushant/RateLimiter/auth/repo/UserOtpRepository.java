package com.sushant.RateLimiter.auth.repo;

import com.sushant.RateLimiter.auth.entity.User;
import com.sushant.RateLimiter.auth.entity.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Deprecated
public interface UserOtpRepository extends JpaRepository<UserOtp,Long> {
    UserOtp findByUserIdentifier(String userIdentifier);
    Optional<UserOtp> findLatestByUserIdentifier(String userIdentifier);
    boolean existsByUserIdentifier(String userIdentifier);
}
