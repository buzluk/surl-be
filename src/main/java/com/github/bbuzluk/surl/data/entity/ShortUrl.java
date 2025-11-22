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
@Table(indexes = {@Index(name = "idx_short_code", columnList = "shortCode", unique = true)})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortUrl {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String originalUrl;

  @Column(nullable = false, unique = true)
  private String shortCode;

  public static ShortUrl create(
      @Nonnull String username, @Nonnull String originalUrl, @Nonnull String shortCode) {
    ShortUrl shortUrl = new ShortUrl();
    shortUrl.setUsername(requireNonNull(username));
    shortUrl.setOriginalUrl(requireNonNull(originalUrl));
    shortUrl.setShortCode(requireNonNull(shortCode));
    return shortUrl;
  }
}
