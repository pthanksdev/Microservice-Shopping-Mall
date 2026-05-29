package com.mall.user.service;

import com.mall.common.exception.BusinessException;
import com.mall.common.exception.DuplicateResourceException;
import com.mall.user.dto.*;
import com.mall.user.model.*;
import com.mall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getAccountType())
                .provider(AuthProvider.LOCAL)
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        // If registering as VENDOR, publish event to vendor-service
        if (request.getAccountType() == Role.VENDOR) {
            kafkaTemplate.send("user.registered.vendor", user.getId());
            log.info("Published vendor registration event for user: {}", user.getId());
        } else {
            kafkaTemplate.send("user.registered", user.getId());
        }

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getRole().name());

        return AuthResponse.of(accessToken, refreshToken, jwtService.getExpirationMs(), UserResponse.from(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (user.getProvider() != AuthProvider.LOCAL) {
            throw new BusinessException("This account uses " + user.getProvider() + " login. Please use OAuth2.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.BANNED) {
            throw new BusinessException("Account is banned. Contact support.");
        }

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getRole().name());

        return AuthResponse.of(accessToken, refreshToken, jwtService.getExpirationMs(), UserResponse.from(user));
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new BusinessException("Invalid or expired refresh token");
        }
        String userId = jwtService.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getRole().name());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getRole().name());

        jwtService.blacklistToken(refreshToken);
        return AuthResponse.of(newAccessToken, newRefreshToken, jwtService.getExpirationMs(), UserResponse.from(user));
    }

    public void logout(String token) {
        jwtService.blacklistToken(token);
        log.info("Token blacklisted on logout");
    }
}
