package com.github.bbuzluk.surl.controller;

import com.github.bbuzluk.surl.data.model.CreateUserRequest;
import com.github.bbuzluk.surl.service.UserService;
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
