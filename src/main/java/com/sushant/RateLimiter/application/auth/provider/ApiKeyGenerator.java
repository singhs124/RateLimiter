package com.sushant.RateLimiter.application.auth.provider;

import com.sushant.RateLimiter.application.auth.util.ApiKeyUtils;
import com.sushant.RateLimiter.application.auth.util.Constants;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

@Component
public class ApiKeyGenerator {
    private static final String PREFIX = Constants.API_KEY_PREFIX;
    private static final Integer RANDOM_LEN = Constants.API_KEY_RANDOM_LEN;
    private static final String DELIMITER = Constants.API_KEY_DELIMITER;

    public String generateKey(String uuid){
        String randomPart = ApiKeyUtils.randomBase62(RANDOM_LEN);

        CRC32 crc32 = new CRC32();
        crc32.update(randomPart.getBytes(StandardCharsets.UTF_8));
        long crcValue = crc32.getValue();

        String checkSum = String.format("%6s", ApiKeyUtils.encode(crcValue)).replace(' ','0');

        return PREFIX + DELIMITER + uuid + DELIMITER + randomPart + DELIMITER + checkSum;
    }

}
