package com.sushant.RateLimiter.auth.service;

import com.sushant.RateLimiter.auth.entity.User;
import com.sushant.RateLimiter.auth.exception.UserNotFoundException;
import com.sushant.RateLimiter.auth.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private UserRepository userRepo;

    @Transactional
    public Optional<User> createUser(String email){
        if(userRepo.existsByEmail(email)){
            log.debug("Existing User");
            return userRepo.findByEmail(email);
        }
        User user = new User();
        user.setEmail(email);
        return Optional.of(userRepo.save(user));
    }

    @Transactional
    public User updateUser(Long Id, String token){
        User user = userRepo.findById(Id).orElseThrow(()->new UserNotFoundException("User not Found!"));
        user.setRefreshToken(token);
        return userRepo.save(user);
    }
}
