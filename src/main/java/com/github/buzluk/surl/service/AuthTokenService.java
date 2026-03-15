package com.github.buzluk.surl.service;

import com.github.buzluk.surl.data.model.AuthToken;
import java.time.Instant;

public interface AuthTokenService {
  AuthToken generate(String value, Instant expiration);

  boolean isValid(String token);

  AuthToken get(String token);

  void invalidate(String token);
}
