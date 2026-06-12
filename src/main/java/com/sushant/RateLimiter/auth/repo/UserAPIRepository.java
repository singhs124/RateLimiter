package com.sushant.RateLimiter.auth.repo;

import com.sushant.RateLimiter.auth.entity.ApiKeyStatus;
import com.sushant.RateLimiter.auth.entity.UserApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAPIRepository extends JpaRepository<UserApiKey, Long> {
    Optional<UserApiKey> findByUserId(Long userId);
    Optional<UserApiKey> findByKeyLookup(String uuid);
    boolean existsByUserIdAndStatusNot(long id, ApiKeyStatus status);

}
