package com.teasound.teasound_api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateInfoUserResponse {
    private String error;
    private String message;
    private String newName;
    private String newAvatarUrl;
}
