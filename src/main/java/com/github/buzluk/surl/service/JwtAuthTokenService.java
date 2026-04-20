package com.github.buzluk.surl.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.buzluk.surl.data.dto.AuthToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.function.BiFunction;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
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
    log.info("JwtAuthTokenService initialized with cache (Max Size: 10000)");
  }

  private static BiFunction<String, AuthToken, Duration> expiryFunction() {
    return (key, authToken) -> Duration.between(Instant.now(), authToken.expiration());
  }

  @Override
  public AuthToken generate(String value, Instant expiration) {
    String jwtToken = createJwtToken(value, Date.from(expiration));
    AuthToken authToken = new AuthToken(value, jwtToken, expiration);
    cache.put(jwtToken, authToken);
    log.debug("Generated new AuthToken for subject: {} with expiration: {}", value, expiration);
    return authToken;
  }

  public String createJwtToken(String subject, Date expiration) {
    return Jwts.builder().subject(subject).expiration(expiration).signWith(secretKey).compact();
  }

  @Override
  public boolean isValid(String token) {
    AuthToken authToken = get(token);
    boolean valid = authToken != null && authToken.isValid();
    log.debug("Token validity check for {}: {}", token, valid);
    return valid;
  }

  @Override
  public AuthToken get(String token) {
    if (token == null) {
      log.debug("Attempted to get AuthToken with null token string.");
      return null;
    }

    AuthToken authToken = cache.getIfPresent(token);
    if (authToken != null) {
      log.debug("AuthToken cache hit for token: {}", token);
      return authToken;
    }

    log.debug("AuthToken cache miss for token: {}. Attempting to parse.", token);
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
    } catch (ExpiredJwtException e) {
      log.debug("JWT token is expired: {}", token, e);
      return null;
    } catch (SecurityException e) {
      log.debug("JWT token signature is invalid: {}", token, e);
      return null;
    } catch (MalformedJwtException e) {
      log.debug("JWT token is malformed: {}", token, e);
      return null;
    } catch (IllegalArgumentException e) {
      log.debug("JWT token is null, empty or has illegal arguments: {}", token, e);
      return null;
    } catch (JwtException e) {
      log.debug("An unexpected JWT error occurred during parsing: {}", token, e);
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
    log.debug("Invalidated AuthToken for token: {}", token);
  }
}
