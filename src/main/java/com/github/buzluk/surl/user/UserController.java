package com.github.buzluk.surl.user;

import com.github.buzluk.surl.user.data.dto.CreateUserRequest;
import com.github.buzluk.surl.user.service.UserService;
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
