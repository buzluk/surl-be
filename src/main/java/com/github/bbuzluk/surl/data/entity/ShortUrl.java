package com.github.bbuzluk.surl.data.entity;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
public class ShortUrl {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreationTimestamp private Date createdAt;

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
    return shortUrl;
  }
}
