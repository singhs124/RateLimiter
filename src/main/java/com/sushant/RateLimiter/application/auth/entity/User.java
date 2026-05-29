package com.sushant.RateLimiter.application.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="rl_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name=  "user_id", nullable = false)
    private Long id;

    @Column(name = "user_name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "plan_type")
    @Enumerated(EnumType.STRING)
    private Plans planType;

    @Column(name = "token")
    private String refreshToken;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
