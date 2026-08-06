package com.teasound.teasound_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String email;
    private String name;
    private String avatarUrl;
    private String role;
    private Long id;
}
