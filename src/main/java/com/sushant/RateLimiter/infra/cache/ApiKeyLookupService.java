package com.sushant.RateLimiter.infra.cache;

import com.sushant.RateLimiter.auth.dto.ApiKeyDTO;
import com.sushant.RateLimiter.auth.entity.ApiKeyStatus;
import com.sushant.RateLimiter.auth.entity.UserApiKey;
import com.sushant.RateLimiter.auth.repo.UserAPIRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiKeyLookupService {

    private final ApiKeyCacheService apiKeyCacheService;
    private final UserAPIRepository userAPIRepository;

    public ApiKeyDTO find(String uuid){
        return apiKeyCacheService.lookup(uuid).orElseGet(()->{
            UserApiKey entity = userAPIRepository.findByKeyLookup(uuid)
                    .orElse(null);
            if(entity == null) return null;

            ApiKeyDTO dto = mapToDto(entity,uuid);
            apiKeyCacheService.populate(uuid,dto);
            return dto;
        });
    }

    public ApiKeyDTO mapToDto(UserApiKey entity, String uuid){
        return new ApiKeyDTO(
                uuid,
                entity.getKeyHashed(),
                entity.getPlanType().toString(),
                entity.getStatus() == ApiKeyStatus.REVOKED
        );
    }

}
