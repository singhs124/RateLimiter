package com.sushant.RateLimiter.auth.service;

import com.sushant.RateLimiter.auth.config.SHAHashing;
import com.sushant.RateLimiter.auth.dto.ApiKeyDTO;
import com.sushant.RateLimiter.auth.dto.AuthRequest;
import com.sushant.RateLimiter.auth.dto.AuthTokenResDTO;
//import com.sushant.RateLimiter.application.auth.entity.*;
import com.sushant.RateLimiter.auth.entity.*;
import com.sushant.RateLimiter.auth.exception.InvalidEmailException;

import com.sushant.RateLimiter.auth.exception.InvalidOTPException;
import com.sushant.RateLimiter.auth.exception.UserNotFoundException;
import com.sushant.RateLimiter.auth.provider.OTPGenerator;
import com.sushant.RateLimiter.auth.repo.AuthRepo;
import com.sushant.RateLimiter.auth.repo.UserRepository;
import com.sushant.RateLimiter.auth.repo.UserAPIRepository;
import com.sushant.RateLimiter.auth.provider.ApiKeyGenerator;
//import com.sushant.RateLimiter.application.auth.util.*;
import com.sushant.RateLimiter.auth.util.*;
import com.sushant.RateLimiter.infra.cache.ApiKeyCacheService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.UUID;

@Data
@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AuthUtils authUtil;
    private final UserService userService;
    private final UserAPIRepository userAPIRepository;
    private final AuthRepo authRepo;
    private final ApiKeyGenerator apiKeyGenerator;
    private final ApiKeyCacheService apiKeyCacheService;
//    private final JavaMailSender javaMailSender;

//    @Value("${mail.sender.name}")
//    private String senderName;
//
//    @Value("${mail.sender.email}")
//    private String senderEmail;

    private boolean validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    public String generateOtp(String email) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, MessagingException, UnsupportedEncodingException {
        if(!validateEmail(email)){
            throw new InvalidEmailException("Invalid Email!");
        }
        String salt = SHAHashing.generateSalt(16);
        String EncryptedEmail = EncryptionUtil.encrypt(email);
        UserOtp checkIfUserExistAlready  = authRepo.findByUserIdentifier(EncryptedEmail);
        if(checkIfUserExistAlready != null){
            log.debug("User ReAttempted to generate OTP");
            String otp = OTPGenerator.generateOtp();
            String hashedOtp = SHAHashing.generateSHA256Hash(otp+salt);
            checkIfUserExistAlready.setSalt(salt);
            checkIfUserExistAlready.setOtpHash(hashedOtp);
            authRepo.save(checkIfUserExistAlready);
//            sendOtp(email,otp);
            return hashedOtp;
        }
        String otp = OTPGenerator.generateOtp();
        String hashedOtp = SHAHashing.generateSHA256Hash(otp+salt);
        UserOtp userOtp = new UserOtp();
        userOtp.setUserIdentifier(EncryptedEmail);
        userOtp.setOtpHash(hashedOtp);
        userOtp.setSalt(salt);
        authRepo.save(userOtp);
//        sendOtp(email,otp);
        return hashedOtp;
    }

    public void registerUser(String email) {
        if (!validateEmail(email))
            throw new InvalidEmailException("Incorrect Email!");
        try {
            if(userRepository.existsByEmail(email)) return;
            User user = new User();
            user.setEmail(email);
            user.setPlanType(Plans.FREE);
            userRepository.save(user);
        } catch (Exception ex) {
            throw new InvalidEmailException("Operation Unsuccessful!");
        }
    }

    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

//    public String generateAPIkey(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new InvalidEmailException("User not found for email: " + email));
//
//        String uuid = ApiKeyUtils.encode(UUID.randomUUID());
//        String plainKey = apiKeyGenerator.generateKey(uuid);
//        String hashedKey = SHAUtils.generateSHA256Hash(plainKey);
//
//        UserApiKey userApiKey = new UserApiKey();
//        userApiKey.setKeyHashed(hashedKey);
//        userApiKey.setUser(user);
//        userApiKey.setStatus(ApiKeyStatus.ACTIVE);
//        userApiKey.setKeyPrefix(plainKey.substring(0, 4));
//        userApiKey.setPlanType(user.getPlanType());
//        userApiKey.setKeyLookup(uuid);
//
//        userAPIRepository.save(userApiKey);
//
////      Todo: if redis is not up, then skip caching, instead of crashing
//        ApiKeyDTO apiKeyDTO = apiKeyCacheService.mapToDto(userApiKey, uuid);
//        apiKeyCacheService.populate(uuid, apiKeyDTO);
//
//        return plainKey;
//    }

//    private void sendOtp(String to, String otp) throws MessagingException, UnsupportedEncodingException {
//        log.debug("Sending Auth to {} : {}", to, otp);
//        MimeMessage message = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message,true);
//        helper.setFrom(senderEmail,senderName);
//        helper.setTo(to);
//        helper.setSubject("Authentication Info");
//        helper.setText("Your OTP is "+ otp + ". It is valid for 5 minutes.");
//        javaMailSender.send(message);
//        log.debug("Email Sent!");
//    }

    public AuthTokenResDTO validateOtp(AuthRequest obj) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String EncryptedUserIdentifier = EncryptionUtil.encrypt(obj.getEmail());
        log.debug("EncryptMobileNumber: {}" , EncryptedUserIdentifier);
        UserOtp record = authRepo.findByUserIdentifier(EncryptedUserIdentifier);
        if(record == null){
            throw new InvalidOTPException("OTP not Generated, Try Again!");
        }
        String salt = record.getSalt();
        String otpWithSalt = obj.getOtp() + salt;
        String HashedOtp = SHAHashing.generateSHA256Hash(otpWithSalt);
        log.debug("HashedOtp: {}", HashedOtp);
        if(!HashedOtp.equals(record.getOtpHash())){
            throw new InvalidOTPException("Incorrect OTP!");
        }
        //Create or Check for this email in Users Table
        User user = userService.createUser(EncryptedUserIdentifier).orElseThrow(()->new UserNotFoundException("User Not Found"));
        return generateTokenWithLogin(user);
    }

    private AuthTokenResDTO generateTokenWithLogin(User user){
        String access = authUtil.generateAccessToken(user.getId().toString());
        String refresh = authUtil.generateRefreshToken(user.getId().toString());
        userService.updateUser(user.getId(),refresh);
        Instant instant = Instant.now();
        return new AuthTokenResDTO(access,refresh,instant.toEpochMilli());
    }

    // Todo: Revoke APi Key
}
