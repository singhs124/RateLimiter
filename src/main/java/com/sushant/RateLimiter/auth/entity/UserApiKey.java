package com.sushant.RateLimiter.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rl_user_api", indexes = {
        @Index(name = "idx_key_hash", columnList = "key_hashed")
})
public class UserApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_hashed")
    private String keyHashed;

    @Column(name = "key_prefix")
    private String keyPrefix;

    @Column(name = "key_lookup")
    private String keyLookup;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ApiKeyStatus status;

    @Column(name = "plan_type")
    @Enumerated(EnumType.STRING)
    private Plans planType; //todo: is it required? Already there in users table

    @UpdateTimestamp
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt; //todo: is it used?

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
}
