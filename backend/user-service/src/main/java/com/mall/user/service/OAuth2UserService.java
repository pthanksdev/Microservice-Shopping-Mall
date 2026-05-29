package com.mall.user.service;

import com.mall.user.model.*;
import com.mall.user.oauth2.GithubOAuth2UserInfo;
import com.mall.user.oauth2.GoogleOAuth2UserInfo;
import com.mall.user.oauth2.OAuth2UserInfo;
import com.mall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = extractUserInfo(registrationId, oAuth2User.getAttributes());

        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());
        Optional<User> existingUser = userRepository.findByEmail(userInfo.getEmail());

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            // Update provider info if logging in via different provider
            if (user.getProvider() != provider) {
                log.info("Linking {} provider to existing account: {}", provider, user.getEmail());
                user.setProvider(provider);
                user.setProviderId(userInfo.getId());
            }
        } else {
            // New OAuth2 user — role defaults to null (PENDING), must complete profile
            user = User.builder()
                    .email(userInfo.getEmail())
                    .firstName(extractFirstName(userInfo.getName()))
                    .lastName(extractLastName(userInfo.getName()))
                    .profileImageUrl(userInfo.getImageUrl())
                    .provider(provider)
                    .providerId(userInfo.getId())
                    .role(Role.CUSTOMER) // Default role; can be changed via /auth/complete-profile
                    .status(UserStatus.ACTIVE)
                    .build();
            log.info("New OAuth2 user registered: {} via {}", user.getEmail(), provider);
        }

        user = userRepository.save(user);

        // Store user ID in attributes for success handler
        Map<String, Object> attributes = new java.util.HashMap<>(oAuth2User.getAttributes());
        attributes.put("userId", user.getId());
        attributes.put("userRole", user.getRole().name());

        return new DefaultOAuth2User(
                Collections.emptyList(),
                attributes,
                "email"
        );
    }

    private OAuth2UserInfo extractUserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            case "github" -> new GithubOAuth2UserInfo(attributes);
            default -> throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
        };
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "User";
        String[] parts = fullName.split(" ");
        return parts[0];
    }

    private String extractLastName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        String[] parts = fullName.split(" ", 2);
        return parts.length > 1 ? parts[1] : "";
    }
}
