package com.github.buzluk.surl.controller;

import com.github.buzluk.surl.data.dto.AuthToken;
import com.github.buzluk.surl.data.dto.LoginRequest;
import com.github.buzluk.surl.service.AuthService;
import jakarta.validation.Valid;
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
  public String login(@Valid @RequestBody LoginRequest request) {
    AuthToken login = authService.login(request.username(), request.password());
    return login.token();
  }
}
