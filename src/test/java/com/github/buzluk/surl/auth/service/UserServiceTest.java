package com.github.buzluk.surl.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.buzluk.surl.data.entity.User;
import com.github.buzluk.surl.data.model.CreateUserRequest;
import com.github.buzluk.surl.repository.UserRepository;
import com.github.buzluk.surl.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @InjectMocks UserService userService;
  @Mock UserRepository userRepository;
  @Mock PasswordEncoder passwordEncoder;

  @Test
  @DisplayName("loadUserByUsername should return UserDetails when user is found")
  void loadUserByUsername_when_userFound() {
    when(userRepository.findByUsername("testuser"))
        .thenReturn(User.from("testuser", "password", "user@mail.com"));
    UserDetails user = userService.loadUserByUsername("testuser");
    assertNotNull(user);
    assertEquals("testuser", user.getUsername());
    assertEquals("password", user.getPassword());
    assertNotNull(user.getAuthorities());
    assertTrue(user.isEnabled());
    assertTrue(user.isAccountNonLocked());
  }

  @Test
  @DisplayName("loadUserByUsername should throw UsernameNotFoundException when user is not found")
  void loadUserByUsername_when_userNotFound() {
    when(userRepository.findByUsername("nonexistent")).thenReturn(null);
    assertThrows(
        UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistent"));
  }

  @Test
  @DisplayName("createUser should save new user with encoded password")
  void createUser() {
    when(passwordEncoder.encode("newpassword")).thenReturn("encoded-pass");

    CreateUserRequest user = new CreateUserRequest("newuser", "newpassword", "newmail@mail.com");
    userService.createUser(user);

    var userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertEquals("newuser", savedUser.getUsername());
    assertEquals("encoded-pass", savedUser.getPassword());
    assertEquals("newmail@mail.com", savedUser.getEmail());
  }
}
