package com.sushant.RateLimiter.auth.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
@Component
public class EncryptionUtil {
    private static final String ALGORITHM = "AES" ;
    private static final String TRANSFORMATION = "AES/GCM/NoPadding" ;
    private static final int GCM_TAG_LENGTH = 128;
    private static final byte[] IV = new byte[12];

    @Value("${aes.encryption.secret-key}")
    private String secretKey;

    private static String SECRET_KEY;

    @PostConstruct
    public void init(){
        SECRET_KEY = secretKey;
    }

    private static SecretKey getSecretKey(){
        byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY);
        return new SecretKeySpec(decodedKey,0, decodedKey.length, ALGORITHM);
    }

    public static String encrypt(String plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, IV);
        cipher.init(Cipher.ENCRYPT_MODE,getSecretKey(),spec);
        log.debug("Encrypting: {}", plainText);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH,IV);
        cipher.init(Cipher.DECRYPT_MODE,getSecretKey(),spec);
        byte[] decoded = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }
}
