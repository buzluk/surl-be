package com.github.bbuzluk.surl.data.entity;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ShortUrl {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Date createdAt;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String originalUrl;

  @Column(nullable = false, unique = true)
  private String shortCode;

  public static ShortUrl from(String username, String originalUrl, String shortCode) {
    ShortUrl shortUrl = new ShortUrl();
    shortUrl.setUsername(username);
    shortUrl.setOriginalUrl(originalUrl);
    shortUrl.setShortCode(shortCode);
    shortUrl.setCreatedAt(new Date());
    return shortUrl;
  }
}
