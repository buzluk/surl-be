package com.github.buzluk.surl.shorturl.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatedShortUrl {
  private Long id;
  private String originalUrl;
  private String fullShortUrl;
  private String createdAt;
}
