package com.sushant.RateLimiter.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Deprecated
@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name="rl_otp_verification" , indexes = {
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

    @Column(name="attempt_count")
    private Integer attemptCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
}
