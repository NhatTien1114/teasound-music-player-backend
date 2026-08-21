package com.teasound.teasound_api.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.teasound.teasound_api.domain.User;
import com.teasound.teasound_api.dto.request.ChangePasswordRequest;
import com.teasound.teasound_api.dto.request.UpdateInfoUserRequest;
import com.teasound.teasound_api.dto.response.ChangePasswordResponse;
import com.teasound.teasound_api.dto.response.UserResponse;
import com.teasound.teasound_api.mapper.UserMapper;
import com.teasound.teasound_api.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    UserMapper userMapper;

    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // 1. Check nếu là tài khoản Google (không có mật khẩu)
        if (user.getPassword() == null) {
            throw new IllegalArgumentException("Tài khoản đăng nhập bằng Google không dùng tính năng này");
        }
        // 2. Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác");
        }
        // 3. Kiểm tra mật khẩu mới không trùng mật khẩu cũ
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }
        // 4. Mã hóa và lưu mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public UserResponse updateInfo(Long id, UpdateInfoUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user, request);
        userRepository.save(user);
        UserResponse response = userMapper.toUserResponse(user);
        return response;
    }
}
