package com.github.bbuzluk.surl.controller;

import com.github.bbuzluk.surl.data.model.AuthToken;
import com.github.bbuzluk.surl.data.model.LoginRequest;
import com.github.bbuzluk.surl.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public String login(@RequestBody LoginRequest request) {
    AuthToken login = authService.login(request.username(), request.password());
    return login.token();
  }
}
