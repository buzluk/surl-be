package com.github.bbuzluk.surl.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class TestUtil {

  public static final String USERNAME = "tester";

  public static void mockSecurityContextHolder() {
    Authentication auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(USERNAME);

    SecurityContext context = mock(SecurityContext.class);
    when(context.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(context);
  }

  public static void clearSecurityContextHolder() {
    SecurityContextHolder.clearContext();
  }
}
