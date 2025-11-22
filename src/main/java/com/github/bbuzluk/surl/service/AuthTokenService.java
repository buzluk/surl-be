package com.github.bbuzluk.surl.service;

import com.github.bbuzluk.surl.data.model.AuthToken;
import java.time.Instant;

public interface AuthTokenService {
  AuthToken generate(String value, Instant expiration);

  boolean isValid(String token);

  AuthToken get(String token);

  void invalidate(String token);
}
