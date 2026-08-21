package com.teasound.teasound_api.dto.response;

import com.teasound.teasound_api.enums.AuthProvider;
import com.teasound.teasound_api.enums.Role;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String email;
    Role role;
    AuthProvider authProvider;
    String displayName;
    String avatarUrl;
    String phoneNumber;
    boolean isActive;
    boolean isPremium;
    LocalDateTime premiumExpiresAt;
}
