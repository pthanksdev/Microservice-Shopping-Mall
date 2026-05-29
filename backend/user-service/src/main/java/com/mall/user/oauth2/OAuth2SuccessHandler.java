package com.mall.user.oauth2;

import com.mall.user.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    // In production, replace with your frontend URL
    private static final String REDIRECT_URI = "http://localhost:3000/oauth2/callback";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = (String) oAuth2User.getAttribute("userId");
        String role = (String) oAuth2User.getAttribute("userRole");

        String accessToken = jwtService.generateAccessToken(userId, role);
        String refreshToken = jwtService.generateRefreshToken(userId, role);

        String redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
                .queryParam("token", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        log.info("OAuth2 login success for userId: {}", userId);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
