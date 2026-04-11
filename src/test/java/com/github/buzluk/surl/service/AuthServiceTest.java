package com.github.buzluk.surl.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;

import com.github.buzluk.surl.data.dto.AuthToken;
import com.github.buzluk.surl.data.dto.SurlProperties;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
  AuthService authService;
  @Mock AuthTokenService authTokenService;
  @Mock AuthenticationManager authenticationManager;

  @BeforeEach
  void setUp() {
    SurlProperties surlProperties =
        new SurlProperties("http://localhost:8080", 5, Duration.ofSeconds(60));
    this.authService = new AuthService(surlProperties, authTokenService, authenticationManager);
  }

  @Test
  @DisplayName("login should return AuthToken when correct credentials are provided")
  void login_when_correctCredentials() {
    when(authTokenService.generate(any(), any())).thenReturn(new AuthToken("user", ""));
    when(authenticationManager.authenticate(any()))
        .thenReturn(authenticated("user", "password", List.of()));

    var authToken = authService.login("user", "password");
    assertEquals("user", authToken.username());
    assertNotNull(authToken.token());
    assertNotNull(authToken.expiration());
  }

  @Test
  @DisplayName("login should throw AuthenticationException when wrong credentials are provided")
  void login_when_wrongCredentials() {
    when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);
    assertThrows(AuthenticationException.class, () -> authService.login("user", "wrongpassword"));
  }
}
