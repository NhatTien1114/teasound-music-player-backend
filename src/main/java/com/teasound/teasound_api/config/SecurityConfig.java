package com.teasound.teasound_api.config;

import java.util.List;

import com.teasound.teasound_api.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, UserDetailsService userDetailsService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // REST API không cần CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll()   // API công khai (nhạc, tìm kiếm, ...)
                        .requestMatchers("/api/auth/**").permitAll()     // API đăng nhập/đăng ký
                        .requestMatchers("/api/user/**").authenticated() // API cần đăng nhập (playlist, yêu thích, ...)
                        .anyRequest().permitAll())                       // Mặc định cho phép truy cập
                .formLogin(form -> form
                        .loginPage(frontendUrl + "/sign-in")
                        .loginProcessingUrl("/api/auth/login")
                        .defaultSuccessUrl(frontendUrl + "?login=success", true)
                        .failureUrl(frontendUrl + "/sign-in?error=true")
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage(frontendUrl + "/sign-in")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)) // Sử dụng service xử lý Google User
                        .defaultSuccessUrl(frontendUrl + "?login=success", true)
                        .failureUrl(frontendUrl + "/sign-in?error=true"))
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessUrl(frontendUrl + "/sign-in")
                        .permitAll());
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(frontendUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // BCrypt mặc định
    }
}
