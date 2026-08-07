package com.teasound.teasound_api.controller;

import java.util.HashMap;
import java.util.Map;

import com.teasound.teasound_api.domain.User;
import com.teasound.teasound_api.dto.request.LoginRequest;
import com.teasound.teasound_api.dto.response.LoginResponse;
import com.teasound.teasound_api.repository.UserRepository;
import com.teasound.teasound_api.security.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Trao đổi token sau khi Google OAuth2 login:
     * Đọc token từ HttpOnly Cookie "oauth2_token", xóa cookie đó và trả token về
     * JSON cho frontend.
     */
    @PostMapping("/token-exchange")
    public ResponseEntity<?> exchangeOAuth2Token(
            @CookieValue(name = "oauth2_token", required = false) String oauth2Token) {

        if (oauth2Token == null || oauth2Token.isBlank()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No OAuth2 cookie");
            error.put("message", "Không tìm thấy token OAuth2 hoặc đã hết hạn");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            String email = jwtUtil.extractEmail(oauth2Token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            // Tạo cookie xóa (maxAge = 0) để hủy oauth2_token ngay sau khi trao đổi
            ResponseCookie cleanCookie = ResponseCookie.from("oauth2_token", "")
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(0)
                    .sameSite("Lax")
                    .build();

            LoginResponse response = LoginResponse.builder()
                    .token(oauth2Token)
                    .email(user.getEmail())
                    .name(user.getDisplayName())
                    .avatarUrl(user.getAvatarUrl())
                    .role(user.getRole().name())
                    .id(user.getId())
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                    .body(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid token");
            error.put("message", "Token không hợp lệ hoặc đã hết hạn");
            return ResponseEntity.status(401).body(error);
        }
    }

    /**
     * Login bằng email/password → trả JWT token qua JSON response.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .email(user.getEmail())
                    .name(user.getDisplayName())
                    .avatarUrl(user.getAvatarUrl())
                    .role(user.getRole().name())
                    .id(user.getId())
                    .build();

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            error.put("message", "Email hoặc mật khẩu không chính xác");
            return ResponseEntity.status(401).body(error);
        }
    }

    /**
     * Kiểm tra trạng thái đăng nhập và trả về thông tin user.
     * Frontend gọi endpoint này (với Bearer token) để biết user đã đăng nhập chưa.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            Map<String, Object> response = new HashMap<>();
            response.put("authenticated", false);
            return ResponseEntity.status(401).body(response);
        }

        // Lấy email từ principal (UserDetails set bởi JwtAuthenticationFilter)
        String email;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            email = principal.toString();
        }

        return userRepository.findByEmail(email)
                .map(user -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("authenticated", true);
                    response.put("id", user.getId());
                    response.put("email", user.getEmail());
                    response.put("name", user.getDisplayName());
                    response.put("avatarUrl", user.getAvatarUrl());
                    response.put("role", user.getRole().name());
                    response.put("createdAt", user.getCreatedAt());
                    return ResponseEntity.ok((Object) response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("authenticated", false);
                    return ResponseEntity.status(401).body(response);
                });
    }

    /**
     * Đăng ký user mới → trả JSON response (sau khi thành công frontend sẽ chuyển sang trang login).
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest registerRequest) {
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        String displayName = registerRequest.getDisplayName();
        String phoneNumber = registerRequest.getPhoneNumber();

        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Missing fields");
            error.put("message", "Email và mật khẩu là bắt buộc");
            return ResponseEntity.badRequest().body(error);
        }

        // Kiểm tra xem email đã tồn tại chưa
        if (userRepository.findByEmail(email).isPresent()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Email exists");
            error.put("message", "Email đã được sử dụng");
            return ResponseEntity.status(409).body(error);
        }

        // Tạo user mới
        User user = User.builder()
                .email(email)
                .displayName(displayName != null && !displayName.isBlank() ? displayName : email.split("@")[0])
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(password))
                .role(User.Role.USER)
                .authProvider(User.AuthProvider.LOCAL)
                .isActive(true)
                .isPremium(false)
                .build();

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đăng ký thành công");
        response.put("email", user.getEmail());

        return ResponseEntity.status(201).body(response);
    }
}
