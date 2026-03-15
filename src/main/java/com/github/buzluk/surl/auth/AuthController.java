package com.github.buzluk.surl.auth;

import com.github.buzluk.surl.auth.data.dto.AuthToken;
import com.github.buzluk.surl.auth.service.AuthService;
import com.github.buzluk.surl.user.data.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public String login(@RequestBody LoginRequest request) {
    AuthToken login = authService.login(request.username(), request.password());
    return login.token();
  }
}
