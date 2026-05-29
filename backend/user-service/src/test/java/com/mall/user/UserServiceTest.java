package com.mall.user;

import com.mall.user.dto.LoginRequest;
import com.mall.user.dto.RegisterRequest;
import com.mall.user.model.AuthProvider;
import com.mall.user.model.Role;
import com.mall.user.model.User;
import com.mall.user.model.UserStatus;
import com.mall.user.repository.UserRepository;
import com.mall.user.service.AuthService;
import com.mall.user.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-123")
                .email("test@example.com")
                .password("encoded_password")
                .firstName("John")
                .lastName("Doe")
                .role(Role.CUSTOMER)
                .provider(AuthProvider.LOCAL)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    void register_withCustomerAccount_shouldSucceed() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setAccountType(Role.CUSTOMER);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(testUser);
        when(jwtService.generateAccessToken(anyString(), anyString())).thenReturn("access_token");
        when(jwtService.generateRefreshToken(anyString(), anyString())).thenReturn("refresh_token");

        var response = authService.register(request);

        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
        verify(kafkaTemplate).send(eq("user.registered"), anyString());
    }

    @Test
    void register_withVendorAccount_shouldPublishVendorEvent() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("vendor@example.com");
        request.setPassword("password123");
        request.setFirstName("Vendor");
        request.setLastName("User");
        request.setAccountType(Role.VENDOR);

        User vendorUser = User.builder().id("v-123").email("vendor@example.com")
                .role(Role.VENDOR).provider(AuthProvider.LOCAL).status(UserStatus.ACTIVE).build();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(vendorUser);
        when(jwtService.generateAccessToken(anyString(), anyString())).thenReturn("token");
        when(jwtService.generateRefreshToken(anyString(), anyString())).thenReturn("refresh");

        authService.register(request);

        verify(kafkaTemplate).send(eq("user.registered.vendor"), anyString());
    }

    @Test
    void login_withValidCredentials_shouldReturnTokens() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateAccessToken(anyString(), anyString())).thenReturn("access");
        when(jwtService.generateRefreshToken(anyString(), anyString())).thenReturn("refresh");

        var response = authService.login(request);

        assertNotNull(response);
        assertEquals("access", response.getAccessToken());
    }

    @Test
    void login_withInvalidPassword_shouldThrow() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrong_password");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(org.springframework.security.authentication.BadCredentialsException.class,
                () -> authService.login(request));
    }

    @Test
    void register_withDuplicateEmail_shouldThrow() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setAccountType(Role.CUSTOMER);

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(com.mall.common.exception.DuplicateResourceException.class,
                () -> authService.register(request));
    }
}
