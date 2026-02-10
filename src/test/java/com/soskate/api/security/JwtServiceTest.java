package com.soskate.api.security;

import com.soskate.api.config.SoskateSecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @Mock
    private SoskateSecurityProperties securityProperties;

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Configure le mock : quand JwtService appelle getJwtSecret(), il reçoit cette valeur
        when(securityProperties.getJwtSecret())
                .thenReturn("dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RzLW9ubHktMTIzNDU2Nzg5MA==");
        when(securityProperties.getJwtExpiration())
                .thenReturn(3600000L);

        // Crée un faux utilisateur pour les tests
        userDetails = User.builder()
                .username("rider@soskate.com")
                .password("password")
                .authorities(List.of())
                .build();

    }

    @Test
    void extractUsername_withValidToken_returnsCorrectEmail() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("rider@soskate.com");
    }

    @Test
    void generateToken_withValidUser_returnsNonEmptyToken() {

        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotNull().isNotEmpty();

    }

    @Test
    void isTokenValid_withValidTokenAndMatchingUser_returnsTrue() {
        String token = jwtService.generateToken(userDetails);

        boolean isTokenValid = jwtService.isTokenValid(token, userDetails);

        assertThat(isTokenValid).isTrue();
    }

    @Test
    void isTokenValid_withWrongUser_returnsFalse() {
        String token = jwtService.generateToken(userDetails);

        UserDetails wrongUserDetails = User.builder()
                .username("skater@skate.com")
                .password("password")
                .authorities(List.of())
                .build();

        boolean isTokenValid = jwtService.isTokenValid(token, wrongUserDetails);

        assertThat(isTokenValid).isFalse();
    }

    @Test
    void isTokenValid_withExpiredToken_throwsExpiredJwtException() {
        when(securityProperties.getJwtExpiration()).thenReturn(-1000L);
        String token = jwtService.generateToken(userDetails);

        assertThatThrownBy(() -> jwtService.isTokenValid(token, userDetails))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }
}
