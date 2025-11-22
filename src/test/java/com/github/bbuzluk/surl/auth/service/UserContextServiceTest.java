package com.github.bbuzluk.surl.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.bbuzluk.surl.service.UserContextService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class UserContextServiceTest {

  @InjectMocks UserContextService userContextService;

  @Test
  @DisplayName("getCurrentUsername returns the username of the authenticated user")
  void getCurrentUsername_when_userIsAuthenticated() {
    try (var mockedStatic = mockStatic(SecurityContextHolder.class)) {
      var authentication = mock(Authentication.class);
      when(authentication.getName()).thenReturn("testUser");
      var securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(authentication);

      mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

      String username = userContextService.getCurrentUsername();
      assertEquals("testUser", username);
    }
  }

  @Test
  @DisplayName("getCurrentUsername returns 'anonymousUser' when SecurityContext is null")
  void getCurrentUsername_when_securityContextIsNull() {
    try (var mockedStatic = mockStatic(SecurityContextHolder.class)) {
      mockedStatic.when(SecurityContextHolder::getContext).thenReturn(null);

      String username = userContextService.getCurrentUsername();
      assertEquals("anonymousUser", username);
    }
  }

  @Test
  @DisplayName("getCurrentUsername returns 'anonymousUser' when Authentication is null")
  void getCurrentUsername_when_authenticationIsNull() {
    try (var mockedStatic = mockStatic(SecurityContextHolder.class)) {
      var securityContext = mock(SecurityContext.class);
      when(securityContext.getAuthentication()).thenReturn(null);

      mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

      String username = userContextService.getCurrentUsername();
      assertEquals("anonymousUser", username);
    }
  }
}
