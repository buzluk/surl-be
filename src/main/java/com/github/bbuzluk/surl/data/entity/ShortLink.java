package com.github.bbuzluk.surl.data.entity;

import static java.util.Objects.requireNonNull;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortLink {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String originalUrl;

  @Column(nullable = false, unique = true)
  private String shortCode;

  public static ShortLink create(
      @Nonnull String username, @Nonnull String originalUrl, @Nonnull String shortCode) {
    ShortLink shortLink = new ShortLink();
    shortLink.setUsername(requireNonNull(username));
    shortLink.setOriginalUrl(requireNonNull(originalUrl));
    shortLink.setShortCode(requireNonNull(shortCode));
    return shortLink;
  }
}
