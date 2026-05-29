package com.sushant.RateLimiter.application.auth.provider;

import com.sushant.RateLimiter.application.auth.util.ApiKeyUtils;
import com.sushant.RateLimiter.application.auth.util.Constants;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

@Component
public class ApiKeyValidator {

    public Object isStructureValid(String apiKey){
        if(apiKey == null) return false;

        String[] parts = apiKey.split("_",4);
        String prefix = parts[0];
        String uuid = parts[1];
        String randomPart = parts[2];
        String checkSum = parts[3];

        if(!prefix.equals(Constants.API_KEY_PREFIX)) return false;
        if(randomPart.length() != 32) return false;

        CRC32 crc32 = new CRC32();
        crc32.update(randomPart.getBytes(StandardCharsets.UTF_8));
        long crc32Value = crc32.getValue();

        String expectedCheckSum = String.format("%6s", ApiKeyUtils.encode(crc32Value)).replace(' ','0');

        if(expectedCheckSum.equals(checkSum)) return (String) uuid;
        return (Boolean) false;

    }

}
