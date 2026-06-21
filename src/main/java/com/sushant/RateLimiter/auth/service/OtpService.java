package com.sushant.RateLimiter.auth.service;

import com.sushant.RateLimiter.auth.dto.AuthRequest;
import com.sushant.RateLimiter.auth.exception.InvalidOTPException;
import com.sushant.RateLimiter.auth.provider.OTPGenerator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Data
@Slf4j
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    private static final Duration OTP_VALIDITY = Duration.ofMinutes(5);
    private static final Integer MAX_ATTEMPT = 5;

    private String redisKey(String email, String purpose){
        return "otp:"+purpose.toLowerCase()+":"+email;
    }

    private String redisAttemptKey(String email, String purpose){
        return "otp:"+purpose.toLowerCase()+":"+email+":attempts";
    }

    //todo: mitigating plain otp storage in redis
    public void generateAndSend(AuthRequest authRequest){
        String otp = OTPGenerator.generateOtp();
        System.out.println(otp); //todo: remove this once email send is enabled
        String email = authRequest.getEmail();

        String otpKey = redisKey(email,"login"); //todo: add purpose during otp generation
        String attemptKey = redisAttemptKey(email, "login");
        redisTemplate.opsForValue().set(otpKey,otp,OTP_VALIDITY);
        redisTemplate.opsForValue().set(attemptKey,"0",OTP_VALIDITY);

//        sendOtp(email,otp);
    }

    public void validate(AuthRequest req){
        String otpKey = redisKey(req.getEmail(), "login");
        String attemptKey = redisAttemptKey(req.getEmail(), "login");
        String storedOtp = redisTemplate.opsForValue().get(otpKey);
        if(storedOtp == null) throw new InvalidOTPException("OTP Expired or not Found");

        String attempts = redisTemplate.opsForValue().get(attemptKey);
        if(attempts != null && Integer.parseInt(attempts) > MAX_ATTEMPT){
            redisTemplate.delete(otpKey);
            throw new InvalidOTPException("Too many Attempts");
        }

        if(!storedOtp.equals(req.getOtp())){
            redisTemplate.opsForValue().increment(attemptKey);
            throw new InvalidOTPException("Incorrect OTP");
        }

        redisTemplate.delete(otpKey);
        redisTemplate.delete(attemptKey);
    }
}
