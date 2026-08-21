package com.teasound.teasound_api.security;

import com.teasound.teasound_api.domain.User;
import com.teasound.teasound_api.enums.AuthProvider;
import com.teasound.teasound_api.enums.Role;
import com.teasound.teasound_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.isActive()) {
                throw new OAuth2AuthenticationException("User account is disabled");
            }
            if (user.getAvatarUrl() == null && picture != null) {
                user.setAvatarUrl(picture);
                userRepository.save(user);
            }
        } else {
            user = User.builder()
                    .email(email)
                    .displayName(name != null ? name : email)
                    .avatarUrl(picture)
                    .role(Role.USER)
                    .authProvider(AuthProvider.GOOGLE)
                    .isActive(true)
                    .isPremium(false)
                    .build();
            userRepository.save(user);
        }

        // Điểm mấu chốt: wrap lại thay vì trả oAuth2User gốc
        return new CustomOAuth2User(oAuth2User, user);
    }
}
