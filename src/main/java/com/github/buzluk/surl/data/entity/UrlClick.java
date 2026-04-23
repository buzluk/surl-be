package com.github.buzluk.surl.data.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@Table(
    name = "url_click",
    indexes = {@Index(name = "idx_short_url_id", columnList = "short_url_id")})
public class UrlClick {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreationTimestamp private Instant clickedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "short_url_id", nullable = false)
  private ShortUrl shortUrl;

  private String ipAddress;

  private String userAgent;
}
