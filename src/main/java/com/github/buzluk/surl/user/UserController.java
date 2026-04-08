package com.github.buzluk.surl.user;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.github.buzluk.surl.user.data.dto.CreateUserRequest;
import com.github.buzluk.surl.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  @GetMapping("/exists")
  public ResponseEntity<Boolean> existsUsernameOrEmail(
      @RequestParam(value = "username", required = false) String username,
      @RequestParam(value = "email", required = false) String email) {
    if (isNotBlank(username) || isNotBlank(email)) {
      return ResponseEntity.ok(userService.existsByUsernameOrEmail(username, email));
    } else {
      return ResponseEntity.badRequest().body(false);
    }
  }
}
