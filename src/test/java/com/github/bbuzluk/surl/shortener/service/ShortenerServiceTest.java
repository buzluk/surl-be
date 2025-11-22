package com.github.bbuzluk.surl.shortener.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.github.bbuzluk.surl.exception.FailedUniqueShortCodeException;
import com.github.bbuzluk.surl.repository.ShortUrlRepository;
import com.github.bbuzluk.surl.service.ShortCodeGenerator;
import com.github.bbuzluk.surl.service.ShortenerService;
import com.github.bbuzluk.surl.service.UserContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class ShortenerServiceTest {

  ShortenerService shortenerService;
  @Mock ShortCodeGenerator shortCodeGenerator;
  @Mock ShortUrlRepository shortUrlRepository;
  @Mock UserContextService userContextService;

  @BeforeEach
  void setUp() {
    this.shortenerService =
        new ShortenerService(shortCodeGenerator, shortUrlRepository, userContextService, 5);
  }

  @Test
  @DisplayName("createShortUrl should return a short code")
  void createShortUrl() {
    when(shortCodeGenerator.generate()).thenReturn("abc123");
    when(userContextService.getCurrentUsername()).thenReturn("testuser");

    String shortCode = shortenerService.createShortCode("https://google.com");
    assertNotNull(shortCode);
    assertEquals("abc123", shortCode);
  }

  @Test
  @DisplayName(
      "createShortUrl should throw FailedUniqueShortCodeException when max attempts exceeded")
  void createShortUrl_when_maxAttemptsExceeded() {
    when(shortCodeGenerator.generate()).thenReturn("abc123");
    when(userContextService.getCurrentUsername()).thenReturn("testuser");
    when(shortUrlRepository.save(any()))
        .thenThrow(new DataIntegrityViolationException("Duplicate"));

    assertThrows(
        FailedUniqueShortCodeException.class,
        () -> shortenerService.createShortCode("https://testurl.com"));
  }
}
