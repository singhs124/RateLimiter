package com.sushant.RateLimiter.infra.config;


import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class ShardRouter {
    private final static int SHARD_COUNT=4;

    public static int resolveShardId(String email){
        CRC32 crc32 = new CRC32();
        crc32.update(email.getBytes(StandardCharsets.UTF_8));
        return (int) (crc32.getValue() % SHARD_COUNT);
    }
}
