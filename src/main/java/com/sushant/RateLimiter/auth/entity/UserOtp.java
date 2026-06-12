package com.sushant.RateLimiter.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="su_otp_verification" , indexes = {
        @Index(name = "idx_user_identifier" , columnList = "user_identifier"),
        @Index(name = "idx_created_at" , columnList = "created_at")
})
public class UserOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "user_identifier" , nullable = false)
    private String userIdentifier;

    @Column(name = "otp" , nullable = false)
    private String otpHash;

    @Column(name = "salt")
    private String salt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
}
