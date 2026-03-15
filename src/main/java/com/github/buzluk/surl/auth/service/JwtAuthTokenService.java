package com.github.buzluk.surl.auth.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.buzluk.surl.auth.data.dto.AuthToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.function.BiFunction;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthTokenService implements AuthTokenService {
  private final SecretKey secretKey;
  private final Cache<String, AuthToken> cache;

  public JwtAuthTokenService(@Value("${jwt.secret}") String jwtSecret) {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    this.cache =
        Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfter(Expiry.creating(expiryFunction()))
            .build();
  }

  private static BiFunction<String, AuthToken, Duration> expiryFunction() {
    return (key, authToken) -> Duration.between(Instant.now(), authToken.expiration());
  }

  @Override
  public AuthToken generate(String value, Instant expiration) {
    String jwtToken = createJwtToken(value, Date.from(expiration));
    AuthToken authToken = new AuthToken(value, jwtToken, expiration);
    cache.put(jwtToken, authToken);
    return authToken;
  }

  public String createJwtToken(String subject, Date expiration) {
    return Jwts.builder().subject(subject).expiration(expiration).signWith(secretKey).compact();
  }

  @Override
  public boolean isValid(String token) {
    AuthToken authToken = get(token);
    return authToken != null && authToken.isValid();
  }

  @Override
  public AuthToken get(String token) {
    if (token == null) return null;

    AuthToken authToken = cache.getIfPresent(token);
    if (authToken != null) return authToken;

    AuthToken parsedToken = parseJwtToken(token);
    if (parsedToken != null) {
      cache.put(token, parsedToken);
    }
    return parsedToken;
  }

  private AuthToken parseJwtToken(String token) {
    try {
      Claims payload = getPayload(token);
      Date expiration = payload.getExpiration();
      return new AuthToken(payload.getSubject(), token, expiration.toInstant());
    } catch (Exception e) {
      return null;
    }
  }

  private Claims getPayload(String token) throws JwtException, IllegalArgumentException {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }

  @Override
  public void invalidate(String token) {
    cache
        .asMap()
        .compute(token, (key, authToken) -> authToken == null ? null : authToken.invalidate());
  }
}
