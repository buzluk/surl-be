package com.github.buzluk.surl.service;

import com.github.buzluk.surl.data.entity.User;
import com.github.buzluk.surl.data.model.CreateUserRequest;
import com.github.buzluk.surl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user != null) return user;
    throw new UsernameNotFoundException("User with username " + username + " not found");
  }

  public void createUser(CreateUserRequest request) {
    String encodedPassword = passwordEncoder.encode(request.password());
    User newUser = User.from(request.username(), encodedPassword, request.email());
    userRepository.save(newUser);
  }
}
