package com.sushant.RateLimiter.auth.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

public class ApiKeyUtils {
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String encode(long value){
        if(value == 0) return "0";
        StringBuilder sb = new StringBuilder();
        while(value>0){
            sb.append(CHARS.charAt((int)(value%62)));
            value /= 62;
        }
        return sb.reverse().toString();
    }

    public static String encode(UUID uuid) {
        String hex = uuid.toString().replace("-", "");
        BigInteger num = new BigInteger(hex, 16);
        StringBuilder sb = new StringBuilder();
        BigInteger base = BigInteger.valueOf(62);
        while (num.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divRem = num.divideAndRemainder(base);
            sb.append(CHARS.charAt(divRem[1].intValue()));
            num = divRem[0];
        }
        return sb.reverse().toString();
    }

    public static String randomBase62(int length){
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0 ; i < length; i++){
            sb.append(CHARS.charAt(random.nextInt(62)));
        }
        return sb.toString();
    }
}


