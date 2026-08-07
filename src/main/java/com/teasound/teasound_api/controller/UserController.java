package com.teasound.teasound_api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teasound.teasound_api.domain.User;
import com.teasound.teasound_api.dto.request.ChangePasswordRequest;
import com.teasound.teasound_api.dto.request.UpdateInfoUserRequest;
import com.teasound.teasound_api.dto.response.ChangePasswordResponse;
import com.teasound.teasound_api.dto.response.UpdateInfoUserResponse;
import com.teasound.teasound_api.repository.UserRepository;
import com.teasound.teasound_api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/{id}/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            ChangePasswordResponse response = ChangePasswordResponse.builder()
                    .error("User not found")
                    .message("Không tìm thấy người dùng")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (user.getPassword() == null) {
            ChangePasswordResponse response = ChangePasswordResponse.builder()
                    .error("Google user")
                    .message("Tài khoản đăng nhập bằng Google không dùng mật khẩu")
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Kiểm tra mật khẩu cũ bằng PasswordEncoder.matches
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            ChangePasswordResponse response = ChangePasswordResponse.builder()
                    .error("Invalid old password")
                    .message("Mật khẩu cũ không chính xác")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Mã hóa mật khẩu mới trước khi lưu vào DB
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateInfoUserResponse> updateInfo(
            @PathVariable Long id,
            @RequestBody UpdateInfoUserRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            UpdateInfoUserResponse response = UpdateInfoUserResponse.builder()
                    .error("User not found")
                    .message("Không tìm thấy người dùng")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        user.setDisplayName(request.getName());
        user.setAvatarUrl(request.getAvatarUrl());
        userRepository.save(user);

        UpdateInfoUserResponse response = UpdateInfoUserResponse.builder()
                .error("Success")
                .message("Cập nhật thông tin thành công")
                .newName(user.getDisplayName())
                .newAvatarUrl(user.getAvatarUrl())
                .build();
        return ResponseEntity.ok(response);
    }
}
