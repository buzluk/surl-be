package com.github.buzluk.surl.config;

import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.*;

import com.github.buzluk.surl.data.dto.AuthToken;
import com.github.buzluk.surl.service.AuthTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
  private static final String BEARER_PREFIX = "Bearer ";

  private final AuthTokenService authTokenService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = getAuthorizationHeader(request);
    if (authTokenService.isValid(token)) {
      AuthToken authToken = authTokenService.get(token);
      UserDetails user = userDetailsService.loadUserByUsername(authToken.username());
      var authenticated = authenticated(user, null, user.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authenticated);
    }

    // Proceed with the filter chain
    filterChain.doFilter(request, response);
  }

  private String getAuthorizationHeader(HttpServletRequest request) {
    String value = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (value == null || !value.startsWith(BEARER_PREFIX)) {
      return null;
    }
    return value.substring(BEARER_PREFIX.length());
  }
}
