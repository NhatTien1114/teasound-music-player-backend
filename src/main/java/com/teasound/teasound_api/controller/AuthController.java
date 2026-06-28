package com.teasound.teasound_api.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.teasound.teasound_api.domain.User;
import com.teasound.teasound_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Kiểm tra trạng thái đăng nhập và trả về thông tin user.
     * Frontend gọi endpoint này để biết user đã đăng nhập chưa.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @AuthenticationPrincipal OAuth2User oAuth2Principal,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = null;
        if (oAuth2Principal != null) {
            email = oAuth2Principal.getAttribute("email");
        } else if (userDetails != null) {
            email = userDetails.getUsername();
        }

        if (email != null) {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Map<String, Object> response = new HashMap<>();
                response.put("authenticated", true);
                response.put("email", user.getEmail());
                response.put("name", user.getDisplayName());
                response.put("avatarUrl", user.getAvatarUrl());
                response.put("role", user.getRole().name());
                return ResponseEntity.ok(response);
            } else if (oAuth2Principal != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("authenticated", true);
                response.put("name", oAuth2Principal.getAttribute("name"));
                response.put("email", oAuth2Principal.getAttribute("email"));
                response.put("avatarUrl", oAuth2Principal.getAttribute("picture"));
                return ResponseEntity.ok(response);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", false);
        return ResponseEntity.status(401).body(response);
    }

    @PostMapping(value = "/register", consumes = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> register(
            @RequestParam String email,
            @RequestParam String displayName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam String password) {

        // Kiểm tra xem email đã tồn tại chưa
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(302)
                    .header("Location", frontendUrl + "/sign-up?error=email_exists")
                    .build();
        }

        // Tạo user mới
        User user = User.builder()
                .email(email)
                .displayName(displayName)
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(password))
                .role(User.Role.USER)
                .authProvider(User.AuthProvider.LOCAL)
                .isActive(true)
                .isPremium(false)
                .build();

        userRepository.save(user);

        // Redirect về trang đăng nhập sau khi đăng ký thành công
        return ResponseEntity.status(302)
                .header("Location", frontendUrl + "/sign-in?registered=true")
                .build();
    }
}
