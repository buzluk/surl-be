package com.github.bbuzluk.surl.data.response;

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
