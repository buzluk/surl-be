package com.github.buzluk.surl.service;

import static com.github.buzluk.surl.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.github.buzluk.surl.data.dto.CreatedShortUrl;
import com.github.buzluk.surl.data.dto.MinimalPage;
import com.github.buzluk.surl.data.dto.SurlProperties;
import com.github.buzluk.surl.data.entity.ShortUrl;
import com.github.buzluk.surl.data.exception.FailedUniqueShortCodeException;
import com.github.buzluk.surl.data.exception.ServiceException;
import com.github.buzluk.surl.data.repository.ShortUrlRepository;
import com.github.buzluk.surl.service.generator.ShortCodeGenerator;
import com.github.buzluk.surl.service.mapper.ShortUrlMapper;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ShortUrlServiceTest {
  SurlProperties surlProperties = new SurlProperties("test.com/", 5, Duration.ZERO);
  ShortUrlMapper shortUrlMapper = Mappers.getMapper(ShortUrlMapper.class);
  @Mock ShortUrlRepository shortUrlRepository;
  @Mock ShortCodeGenerator shortCodeGenerator;
  UserContextService userContextService = new UserContextService();
  ShortUrlService shortUrlService;

  @BeforeEach
  void setUp() {
    shortUrlMapper.setProperties(surlProperties);
    shortUrlService =
        new ShortUrlService(
            surlProperties,
            shortUrlMapper,
            shortUrlRepository,
            shortCodeGenerator,
            userContextService);
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
              arg.setCreatedAt(new Date());
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
  @DisplayName("getAllShortUrls should return a page of short urls for the current user")
  void getAllShortUrls() {
    List<ShortUrl> expectedShortUrls = createMockShortUrls();
    Page<ShortUrl> expectedPage =
        new PageImpl<>(expectedShortUrls, PAGEABLE, expectedShortUrls.size());
    when(shortUrlRepository.findAllByUsername(USERNAME, PAGEABLE)).thenReturn(expectedPage);

    MinimalPage<CreatedShortUrl> actualPage = shortUrlService.getAllShortUrls(PAGEABLE);

    assertEquals(USERNAME, userContextService.getCurrentUsername());
    assertEquals(expectedShortUrls.size(), actualPage.getContent().size());

    List<CreatedShortUrl> actualShortUrls = actualPage.getContent();
    for (int i = 0; i < expectedShortUrls.size(); i++) {
      var expectedResult = expectedShortUrls.get(i);
      var actualResult = actualShortUrls.get(i);

      assertEquals(expectedResult.getId(), actualResult.getId());
      assertEquals(expectedResult.getOriginalUrl(), actualResult.getOriginalUrl());

      String expectedFormat = DateFormatUtils.format(expectedResult.getCreatedAt(), "dd-MM-YYYY");
      assertEquals(expectedFormat, actualResult.getCreatedAt());

      String expectedFullShortUrl = surlProperties.baseUrl() + expectedResult.getShortCode();
      assertEquals(expectedFullShortUrl, actualResult.getFullShortUrl());
    }
  }

  @Test
  @DisplayName("deleteShortUrl should delete the short URL if the user is the owner")
  void deleteShortUrl_when_userIsOwner() {
    Long shortUrlId = 1L;
    ShortUrl shortUrl = new ShortUrl();
    shortUrl.setUsername(USERNAME);
    shortUrl.setOriginalUrl("https://example.com");
    shortUrl.setShortCode("code1");
    shortUrl.setId(shortUrlId);

    when(shortUrlRepository.findById(shortUrlId)).thenReturn(Optional.of(shortUrl));
    doNothing().when(shortUrlRepository).deleteById(shortUrlId);

    shortUrlService.deleteShortUrl(shortUrlId);

    verify(shortUrlRepository, times(1)).deleteById(shortUrlId);
  }

  @Test
  @DisplayName("deleteShortUrl should throw ServiceException if short URL does not exist")
  void deleteShortUrl_when_shortUrlDoesNotExist() {
    Long shortUrlId = 1L;
    when(shortUrlRepository.findById(shortUrlId)).thenReturn(Optional.empty());

    assertThrows(ServiceException.class, () -> shortUrlService.deleteShortUrl(shortUrlId));
    verify(shortUrlRepository, never()).deleteById(any());
  }

  @Test
  @DisplayName("deleteShortUrl should throw ServiceException if user is not the owner")
  void deleteShortUrl_when_userIsNotOwner() {
    Long shortUrlId = 1L;
    ShortUrl shortUrl = new ShortUrl();
    shortUrl.setUsername("anotherUser");
    shortUrl.setOriginalUrl("https://example.com");
    shortUrl.setShortCode("code1");
    shortUrl.setId(shortUrlId);

    when(shortUrlRepository.findById(shortUrlId)).thenReturn(Optional.of(shortUrl));

    assertThrows(ServiceException.class, () -> shortUrlService.deleteShortUrl(shortUrlId));
    verify(shortUrlRepository, never()).deleteById(any());
  }

  @Test
  @DisplayName("getShortUrlEntity should return ShortUrl when short code exists")
  void getShortUrlEntity_when_shortCodeExists() {
    String shortCode = "abc123";
    String originalUrl = "https://example.com";
    ShortUrl shortUrl = new ShortUrl();
    shortUrl.setUsername(USERNAME);
    shortUrl.setOriginalUrl(originalUrl);
    shortUrl.setShortCode(shortCode);

    when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(shortUrl));

    ShortUrl result = shortUrlService.getShortUrlEntity(shortCode);

    assertEquals(shortUrl, result);
  }

  @Test
  @DisplayName("getShortUrlEntity should throw ServiceException when short code does not exist")
  void getShortUrlEntity_when_shortCodeDoesNotExist() {
    String shortCode = "nonexistent";

    when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

    assertThrows(ServiceException.class, () -> shortUrlService.getShortUrlEntity(shortCode));
  }

  @Test
  @DisplayName("getOriginalUrl should return original URL when short code exists")
  void getOriginalUrl_when_shortCodeExists() {
    String shortCode = "abc123";
    String originalUrl = "https://example.com";
    ShortUrl shortUrl = new ShortUrl();
    shortUrl.setUsername(USERNAME);
    shortUrl.setOriginalUrl(originalUrl);
    shortUrl.setShortCode(shortCode);

    when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(shortUrl));

    String result = shortUrlService.getOriginalUrl(shortCode);

    assertEquals(originalUrl, result);
  }

  @Test
  @DisplayName("getOriginalUrl should throw ServiceException when short code does not exist")
  void getOriginalUrl_when_shortCodeDoesNotExist() {
    String shortCode = "nonexistent";

    when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

    assertThrows(ServiceException.class, () -> shortUrlService.getOriginalUrl(shortCode));
  }

  @Test
  @DisplayName("getAllShortUrls should adjust pageable when sorting by fullShortUrl")
  void getAllShortUrls_with_fullShortUrlSort() {
    org.springframework.data.domain.Pageable pageable =
        org.springframework.data.domain.PageRequest.of(
            0, 10, org.springframework.data.domain.Sort.by("fullShortUrl").descending());

    List<ShortUrl> expectedShortUrls = createMockShortUrls();
    Page<ShortUrl> expectedPage =
        new PageImpl<>(expectedShortUrls, pageable, expectedShortUrls.size());

    // Captured adjusted pageable
    ArgumentCaptor<org.springframework.data.domain.Pageable> captor =
        ArgumentCaptor.forClass(org.springframework.data.domain.Pageable.class);

    when(shortUrlRepository.findAllByUsername(eq(USERNAME), captor.capture()))
        .thenReturn(expectedPage);

    shortUrlService.getAllShortUrls(pageable);

    org.springframework.data.domain.Pageable adjusted = captor.getValue();
    assertNotNull(adjusted.getSort().getOrderFor("originalUrl"));
    assertNotNull(adjusted.getSort().getOrderFor("shortCode"));
    assertNull(adjusted.getSort().getOrderFor("fullShortUrl"));
    assertEquals(
        org.springframework.data.domain.Sort.Direction.DESC,
        adjusted.getSort().getOrderFor("originalUrl").getDirection());
  }

  private static List<ShortUrl> createMockShortUrls() {
    ShortUrl code1 = new ShortUrl();
    code1.setUsername(USERNAME);
    code1.setOriginalUrl("https://example.com");
    code1.setShortCode("code1");
    code1.setCreatedAt(new Date(100000L));
    code1.setId(1L);
    ShortUrl code2 = new ShortUrl();
    code2.setUsername(USERNAME);
    code2.setOriginalUrl("https://example.org");
    code2.setShortCode("code2");
    code2.setCreatedAt(new Date(200000L));
    code2.setId(2L);
    return List.of(code1, code2);
  }
}
