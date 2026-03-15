package com.github.buzluk.surl.shorturl;

import static com.github.buzluk.surl.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.github.buzluk.surl.shorturl.data.dto.CreatedShortUrl;
import com.github.buzluk.surl.shorturl.data.entity.ShortUrl;
import com.github.buzluk.surl.shorturl.exception.FailedUniqueShortCodeException;
import com.github.buzluk.surl.shorturl.generator.ShortCodeGenerator;
import com.github.buzluk.surl.system.data.SurlProperties;
import com.github.buzluk.surl.user.service.UserContextService;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

      String expectedFullShortUrl = surlProperties.baseUrl() + expectedResult.getShortCode();
      assertEquals(expectedFullShortUrl, actualResult.getFullShortUrl());
    }
  }

  @Test
  @DisplayName("deleteShortUrl should delete the short URL if the user is the owner")
  void deleteShortUrl_when_userIsOwner() {
    Long shortUrlId = 1L;
    ShortUrl shortUrl = ShortUrl.from(USERNAME, "https://example.com", "code1");
    shortUrl.setId(shortUrlId);

    when(shortUrlRepository.findById(shortUrlId)).thenReturn(Optional.of(shortUrl));
    doNothing().when(shortUrlRepository).deleteById(shortUrlId);

    shortUrlService.deleteShortUrl(shortUrlId);

    verify(shortUrlRepository, times(1)).deleteById(shortUrlId);
  }

  @Test
  @DisplayName("deleteShortUrl should throw NOT_FOUND if short URL does not exist")
  void deleteShortUrl_when_shortUrlDoesNotExist() {
    Long shortUrlId = 1L;
    when(shortUrlRepository.findById(shortUrlId)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> shortUrlService.deleteShortUrl(shortUrlId));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("ShortUrl not found", exception.getReason());
    verify(shortUrlRepository, never()).deleteById(any());
  }

  @Test
  @DisplayName("deleteShortUrl should throw FORBIDDEN if user is not the owner")
  void deleteShortUrl_when_userIsNotOwner() {
    Long shortUrlId = 1L;
    ShortUrl shortUrl = ShortUrl.from("anotherUser", "https://example.com", "code1");
    shortUrl.setId(shortUrlId);

    when(shortUrlRepository.findById(shortUrlId)).thenReturn(Optional.of(shortUrl));

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> shortUrlService.deleteShortUrl(shortUrlId));

    assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    assertEquals("You are not authorized to delete this ShortUrl", exception.getReason());
    verify(shortUrlRepository, never()).deleteById(any());
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
