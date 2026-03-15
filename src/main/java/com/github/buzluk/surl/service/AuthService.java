package com.github.buzluk.surl.service;

import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

import com.github.buzluk.surl.data.model.AuthToken;
import com.github.buzluk.surl.data.model.SurlConfig;
import java.time.Duration;
import java.time.Instant;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final AuthTokenService authTokenService;
  private final Duration tokenDuration;
  private final AuthenticationManager authenticationManager;

  public AuthService(
      SurlConfig surlConfig,
      AuthTokenService authTokenService,
      AuthenticationManager authenticationManager) {
    this.tokenDuration = surlConfig.authTokenDuration();
    this.authTokenService = authTokenService;
    this.authenticationManager = authenticationManager;
  }

  public AuthToken login(String username, String password) throws AuthenticationException {
    var unauthenticated = unauthenticated(username, password);
    authenticationManager.authenticate(unauthenticated);
    Instant expirationTime = Instant.now().plus(tokenDuration);
    return authTokenService.generate(username, expirationTime);
  }
}
