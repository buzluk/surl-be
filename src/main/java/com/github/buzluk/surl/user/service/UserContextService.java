package com.github.buzluk.surl.user.service;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {

  public boolean isActiveUser(@Nullable String username) {
    if (username == null || username.isBlank()) return false;
    return getCurrentUsername().equals(username);
  }

  @Nonnull
  public String getCurrentUsername() {
    Authentication authentication = getAuthentication();
    return authentication == null ? "" : authentication.getName();
  }

  @Nullable
  private Authentication getAuthentication() {
    SecurityContext context = getContext();
    return context == null ? null : context.getAuthentication();
  }

  @Nullable
  private SecurityContext getContext() {
    return SecurityContextHolder.getContext();
  }
}
