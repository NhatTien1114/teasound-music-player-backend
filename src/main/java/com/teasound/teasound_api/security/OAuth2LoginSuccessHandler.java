package com.teasound.teasound_api.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.teasound.teasound_api.domain.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Sau khi Google OAuth2 login thành công:
 * 1. Lấy CustomOAuth2User từ Authentication
 * 2. Tạo JWT token
 * 3. Đặt JWT vào HttpOnly Cookie tạm thời "oauth2_token" (tồn tại 60 giây)
 * 4. Redirect về frontend với URL sạch: {frontendUrl}?login=success (không lộ token)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        log.info("OAuth2 login success for user: {}, setting HttpOnly cookie and redirecting", user.getEmail());

        // Đặt token vào HttpOnly Cookie ngắn hạn (60 giây)
        ResponseCookie cookie = ResponseCookie.from("oauth2_token", token)
                .httpOnly(true)
                .secure(false) // Chuyển thành true nếu chạy HTTPS ở production
                .path("/")
                .maxAge(60)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Redirect về frontend với URL sạch không chứa token
        String redirectUrl = frontendUrl + "?login=success";
        response.sendRedirect(redirectUrl);
    }
}
