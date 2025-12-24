package com.github.bbuzluk.surl.service;

import static com.github.bbuzluk.surl.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.github.bbuzluk.surl.data.entity.ShortUrl;
import com.github.bbuzluk.surl.data.mapper.ShortUrlMapper;
import com.github.bbuzluk.surl.data.model.SurlConfig;
import com.github.bbuzluk.surl.data.response.CreatedShortUrl;
import com.github.bbuzluk.surl.exception.FailedUniqueShortCodeException;
import com.github.bbuzluk.surl.repository.ShortUrlRepository;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {
  SurlConfig surlConfig = new SurlConfig("test.com/", 5, Duration.ZERO);
  ShortUrlMapper shortUrlMapper = Mappers.getMapper(ShortUrlMapper.class);
  @Mock ShortUrlRepository shortUrlRepository;
  @Mock ShortCodeGenerator shortCodeGenerator;
  UserContextService userContextService = new UserContextService();
  ShortUrlService shortUrlService;

  @BeforeEach
  void setUp() {
    shortUrlMapper.setConfig(surlConfig);
    shortUrlService =
        new ShortUrlService(
            surlConfig, shortUrlMapper, shortUrlRepository, shortCodeGenerator, userContextService);
    mockSecurityContextHolder();
  }

  @AfterEach
  void tearDown() {
    clearSecurityContextHolder();
  }

  @Test
  @DisplayName("createShortUrl should return a short code")
  void createShortUrl() {
    when(shortCodeGenerator.generate()).thenReturn("abc123");
    when(shortUrlRepository.existsByShortCode(any())).thenReturn(false);
    when(shortUrlRepository.save(any()))
        .thenAnswer(
            invocation -> {
              ShortUrl arg = invocation.getArgument(0);
              arg.setId(1L);
              return arg;
            });

    CreatedShortUrl result = shortUrlService.createShortUrl("https://google.com");

    assertEquals(1L, result.getId());
    assertNotNull(result.getCreatedAt());
    assertEquals("https://google.com", result.getOriginalUrl());
    assertEquals("test.com/abc123", result.getFullShortUrl());
  }

  @Test
  @DisplayName(
      "createShortUrl should throw FailedUniqueShortCodeException when max attempts exceeded")
  void createShortUrl_when_maxAttemptsExceeded() {
    when(shortUrlRepository.existsByShortCode(any())).thenReturn(true);

    assertThrows(
        FailedUniqueShortCodeException.class,
        () -> shortUrlService.createShortUrl("https://testurl.com"));
  }

  @Test
  @DisplayName("getAllShortUrls should return a list of short urls for the current user")
  void getAllShortUrls() {
    List<ShortUrl> expectedShortUrls = createMockShortUrls();
    when(shortUrlRepository.findAllByUsername(USERNAME)).thenReturn(expectedShortUrls);

    List<CreatedShortUrl> actualShortUrls = shortUrlService.getAllShortUrls();

    assertEquals(USERNAME, userContextService.getCurrentUsername());

    for (int i = 0; i < expectedShortUrls.size(); i++) {
      var expectedResult = expectedShortUrls.get(i);
      var actualResult = actualShortUrls.get(i);

      assertEquals(expectedResult.getId(), actualResult.getId());
      assertEquals(expectedResult.getOriginalUrl(), actualResult.getOriginalUrl());

      String expectedFormat = DateFormatUtils.format(expectedResult.getCreatedAt(), "dd-MM-YYYY");
      assertEquals(expectedFormat, actualResult.getCreatedAt());

      String expectedFullShortUrl = surlConfig.baseUrl() + expectedResult.getShortCode();
      assertEquals(expectedFullShortUrl, actualResult.getFullShortUrl());
    }
  }

  private static List<ShortUrl> createMockShortUrls() {
    ShortUrl code1 = ShortUrl.from(USERNAME, "https://example.com", "code1");
    code1.setCreatedAt(new Date(100000L));
    code1.setId(1L);
    ShortUrl code2 = ShortUrl.from(USERNAME, "https://example.org", "code2");
    code2.setCreatedAt(new Date(200000L));
    code2.setId(2L);
    return List.of(code1, code2);
  }
}
