package com.teasound.teasound_api.service;

import com.teasound.teasound_api.domain.User;
import com.teasound.teasound_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Autowired
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate to the default implementation for loading a user
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Extract user details from Google
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        // Check if user exists, otherwise register a new one
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update user details if needed, e.g., if they changed their Google name or avatar
            // But usually we respect what's in the DB if it was already updated locally.
            // For now, let's just make sure they are active.
            if (!user.isActive()) {
                throw new OAuth2AuthenticationException("User account is disabled");
            }
            
            // You can optionally update the avatar here if it's null
            if (user.getAvatarUrl() == null && picture != null) {
                user.setAvatarUrl(picture);
                userRepository.save(user);
            }
        } else {
            // Register new user
            user = User.builder()
                    .email(email)
                    .displayName(name != null ? name : email)
                    .avatarUrl(picture)
                    .role(User.Role.USER)
                    .authProvider(User.AuthProvider.GOOGLE)
                    .isActive(true)
                    .isPremium(false)
                    .build();
            userRepository.save(user);
        }

        return oAuth2User; // Or you can return a custom DefaultOAuth2User implementing your own details
    }
}
