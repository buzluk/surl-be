package com.github.buzluk.surl.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.buzluk.surl.data.dto.CreatedShortUrl;
import com.github.buzluk.surl.data.dto.SurlProperties;
import com.github.buzluk.surl.data.entity.ShortUrl;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ShortUrlMapperTest {

  ShortUrlMapper mapper = Mappers.getMapper(ShortUrlMapper.class);
  SurlProperties properties = new SurlProperties("https://surl.com/", 5, Duration.ofHours(1));

  @BeforeEach
  void setUp() {
    mapper.setProperties(properties);
  }

  @Test
  @DisplayName("toCreatedShortUrl should map ShortUrl entity to CreatedShortUrl DTO")
  void toCreatedShortUrl() {
    ShortUrl entity = new ShortUrl();
    entity.setId(1L);
    entity.setShortCode("abc123");
    entity.setOriginalUrl("https://google.com");

    Date createdAt = Date.from(Instant.parse("2026-04-11T00:00:00Z"));
    entity.setCreatedAt(createdAt);

    CreatedShortUrl dto = mapper.toCreatedShortUrl(entity);

    assertNotNull(dto);
    assertEquals(1L, dto.getId());
    assertEquals("https://google.com", dto.getOriginalUrl());
    assertEquals("https://surl.com/abc123", dto.getFullShortUrl());
    assertEquals("11-04-2026", dto.getCreatedAt());
  }
}
