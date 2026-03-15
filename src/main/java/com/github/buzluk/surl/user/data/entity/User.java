package com.github.buzluk.surl.user.data.entity;

import static java.util.Objects.requireNonNull;

import com.github.buzluk.surl.user.data.enums.UserStatus;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.Set;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Setter
@Getter
@Table(
    name = "users",
    indexes = {
      @Index(name = "idx_username", columnList = "username", unique = true),
      @Index(name = "idx_email", columnList = "email", unique = true)
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus status = UserStatus.ACTIVE;

  public static User from(String username, String password, String email) {
    User user = new User();
    user.setUsername(requireNonNull(username));
    user.setPassword(requireNonNull(password));
    user.setEmail(requireNonNull(email));
    return user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Set.of(() -> "ROLE_USER");
  }

  @Override
  public boolean isEnabled() {
    return status == UserStatus.ACTIVE;
  }

  @Override
  public boolean isAccountNonLocked() {
    return status != UserStatus.LOCKED;
  }
}
