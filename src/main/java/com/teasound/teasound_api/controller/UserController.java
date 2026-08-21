package com.teasound.teasound_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teasound.teasound_api.dto.request.ChangePasswordRequest;
import com.teasound.teasound_api.dto.request.UpdateInfoUserRequest;
import com.teasound.teasound_api.dto.response.ApiResponse;
import com.teasound.teasound_api.dto.response.UserResponse;
import com.teasound.teasound_api.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PreAuthorize("#id == authentication.principal.id")
    @PostMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Password changed successfully")
                .build());
    }

    @PreAuthorize("#id == authentication.principal.id")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateInfo(
            @PathVariable Long id,
            @RequestBody UpdateInfoUserRequest request) {
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .code(200)
                .message("User updated successfully")
                .result(userService.updateInfo(id, request))
                .build());
    }
}
