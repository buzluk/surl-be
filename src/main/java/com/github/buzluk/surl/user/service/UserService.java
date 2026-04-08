package com.github.buzluk.surl.user.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.github.buzluk.surl.core.exception.ServiceException;
import com.github.buzluk.surl.user.UserRepository;
import com.github.buzluk.surl.user.data.dto.CreateUserRequest;
import com.github.buzluk.surl.user.data.entity.User;
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

  public boolean existsByUsernameOrEmail(String username, String email) {
    if (isNotBlank(username)) {
      return userRepository.existsByUsername(username);
    }
    if (isNotBlank(email)) {
      return userRepository.existsByEmail(email);
    }
    throw new ServiceException("Username or email must be provided");
  }
}
