package com.sushant.RateLimiter.application.auth.repo;

import com.sushant.RateLimiter.application.auth.entity.UserApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAPIRepository extends JpaRepository<UserApiKey, Long> {
    Optional<UserApiKey> findByUserId(Long userId);
    Optional<UserApiKey> findByKeyLookup(String uuid);
}
