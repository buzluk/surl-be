package com.github.bbuzluk.surl.service;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {

  @Nonnull
  public String getCurrentUsername() {
    Authentication authentication = getAuthentication();
    return authentication == null ? "anonymousUser" : authentication.getName();
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
