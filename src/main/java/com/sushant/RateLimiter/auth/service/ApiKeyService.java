package com.sushant.RateLimiter.auth.service;

import com.sushant.RateLimiter.auth.dto.ApiKeyDTO;
import com.sushant.RateLimiter.auth.entity.ApiKeyStatus;
import com.sushant.RateLimiter.auth.entity.User;
import com.sushant.RateLimiter.auth.entity.UserApiKey;
import com.sushant.RateLimiter.auth.exception.InvalidEmailException;
import com.sushant.RateLimiter.auth.provider.ApiKeyGenerator;
import com.sushant.RateLimiter.auth.repo.UserAPIRepository;
import com.sushant.RateLimiter.auth.repo.UserRepository;
import com.sushant.RateLimiter.auth.util.ApiKeyUtils;
import com.sushant.RateLimiter.auth.util.SHAUtils;
import com.sushant.RateLimiter.infra.cache.ApiKeyCacheService;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Data
public class ApiKeyService {

    private final UserRepository userRepository;
    private final UserAPIRepository userAPIRepository;
    private final ApiKeyGenerator apiKeyGenerator;
    private final ApiKeyCacheService apiKeyCacheService;

    public String generateAPIkey(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidEmailException("User not found for email: " + email));
        boolean activeUserExists = userAPIRepository.existsByUserIdAndStatusNot(user.getId(), ApiKeyStatus.REVOKED);
        if(activeUserExists) return "You already have an Active Key for this Account";

        String uuid = ApiKeyUtils.encode(UUID.randomUUID());
        String plainKey = apiKeyGenerator.generateKey(uuid);
        String hashedKey = SHAUtils.generateSHA256Hash(plainKey);

        UserApiKey userApiKey = new UserApiKey();
        userApiKey.setKeyHashed(hashedKey);
        userApiKey.setUser(user);
        userApiKey.setStatus(ApiKeyStatus.ACTIVE);
        userApiKey.setKeyPrefix(plainKey.substring(0, 4));
        userApiKey.setPlanType(user.getPlanType());
        userApiKey.setKeyLookup(uuid);

        userAPIRepository.save(userApiKey);

//      Todo: if redis is not up, then skip caching, instead of crashing
        ApiKeyDTO apiKeyDTO = apiKeyCacheService.mapToDto(userApiKey, uuid);
        apiKeyCacheService.populate(uuid, apiKeyDTO);

        return plainKey;
    }
}
