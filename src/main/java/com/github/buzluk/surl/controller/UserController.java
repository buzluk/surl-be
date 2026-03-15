package com.github.buzluk.surl.controller;

import com.github.buzluk.surl.data.model.CreateUserRequest;
import com.github.buzluk.surl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @PostMapping
  public void createUser(@RequestBody CreateUserRequest request) {
    userService.createUser(request);
  }
}
