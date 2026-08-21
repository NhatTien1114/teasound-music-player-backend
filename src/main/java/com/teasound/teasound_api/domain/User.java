package com.teasound.teasound_api.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.teasound.teasound_api.enums.AuthProvider;
import com.teasound.teasound_api.enums.Role;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Tài khoản ──────────────────────────────
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true) // null nếu đăng nhập bằng Google
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider = AuthProvider.LOCAL;

    // ── Thông tin cá nhân ──────────────────────
    @Column(nullable = false)
    private String displayName;

    private String avatarUrl;

    private String phoneNumber;

    // ── Trạng thái ─────────────────────────────
    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private boolean isPremium = false;

    private LocalDateTime premiumExpiresAt;

    // ── Audit ───────────────────────────────────
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}