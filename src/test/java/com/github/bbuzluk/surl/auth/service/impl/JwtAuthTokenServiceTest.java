package com.github.bbuzluk.surl.auth.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.github.bbuzluk.surl.data.model.AuthToken;
import com.github.bbuzluk.surl.service.impl.JwtAuthTokenService;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtAuthTokenServiceTest {
  JwtAuthTokenService jwtAuthTokenService;

  @BeforeEach
  void setUp() {
    jwtAuthTokenService =
        new JwtAuthTokenService("54d95566390ff1414e6a383a7105f9a190b6d2e632d6ac76");
  }

  @Test
  @DisplayName("generate should create a valid AuthToken")
  void generate() {
    Instant expiration = Instant.now().plusSeconds(180);
    AuthToken result = jwtAuthTokenService.generate("user", expiration);
    assertEquals("user", result.username());
    assertNotNull(result.token());
    assertEquals(expiration, result.expiration());
  }

  @Test
  @DisplayName("isValid should return true for valid token and false for invalid token")
  void isValid() {
    Instant expiration = Instant.now().plusSeconds(180);
    AuthToken authToken = jwtAuthTokenService.generate("user", expiration);
    assertTrue(jwtAuthTokenService.isValid(authToken.token()));
    assertFalse(jwtAuthTokenService.isValid("invalid.token.here"));
  }

  @Test
  @DisplayName("get should return AuthToken for valid token and null for invalid token")
  void get() {
    Instant expiration = Instant.now().plusSeconds(180);
    AuthToken authToken = jwtAuthTokenService.generate("user", expiration);
    AuthToken fetchedToken = jwtAuthTokenService.get(authToken.token());
    assertNotNull(fetchedToken);
    assertEquals("user", fetchedToken.username());
    assertEquals(authToken.token(), fetchedToken.token());
    assertEquals(expiration, fetchedToken.expiration());

    AuthToken invalidToken = jwtAuthTokenService.get("invalid.token.here");
    assertNull(invalidToken);
  }

  @Test
  @DisplayName("get should return AuthToken when valid token is provided")
  void get_when_validTokenProvided() {
    Instant expiration = Instant.parse("9999-01-01T00:00:00Z");
    String token = jwtAuthTokenService.createJwtToken("user", Date.from(expiration));
    AuthToken fetchedToken = jwtAuthTokenService.get(token);
    assertNotNull(fetchedToken);
    assertEquals(token, fetchedToken.token());
    assertEquals("user", fetchedToken.username());
    assertEquals(expiration, fetchedToken.expiration());
  }

  @Test
  @DisplayName("get should return null when token is null")
  void get_when_tokenIsNull() {
    AuthToken fetchedToken = jwtAuthTokenService.get(null);
    assertNull(fetchedToken);
  }

  @Test
  @DisplayName("invalidate should invalidate a valid token")
  void invalidate() {
    Instant expiration = Instant.now().plusSeconds(180);
    AuthToken authToken = jwtAuthTokenService.generate("user", expiration);
    assertTrue(jwtAuthTokenService.isValid(authToken.token()));

    jwtAuthTokenService.invalidate(authToken.token());
    assertFalse(jwtAuthTokenService.isValid(authToken.token()));
  }

  @Test
  @DisplayName("invalidate should handle non-existent token gracefully")
  void invalidate_when_nonExistentToken() {
    String nonExistentToken = "non.existent.token";
    assertDoesNotThrow(() -> jwtAuthTokenService.invalidate(nonExistentToken));
  }
}
