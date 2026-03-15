package com.github.buzluk.surl.shorturl;

import com.github.buzluk.surl.shorturl.data.dto.CreatedShortUrl;
import com.github.buzluk.surl.shorturl.data.entity.ShortUrl;
import com.github.buzluk.surl.shorturl.exception.FailedUniqueShortCodeException;
import com.github.buzluk.surl.shorturl.generator.ShortCodeGenerator;
import com.github.buzluk.surl.system.data.SurlProperties;
import com.github.buzluk.surl.user.service.UserContextService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ShortUrlService {
  private final SurlProperties surlProperties;
  private final ShortUrlMapper shortUrlMapper;
  private final ShortUrlRepository shortUrlRepository;
  private final ShortCodeGenerator shortCodeGenerator;
  private final UserContextService userContextService;

  public CreatedShortUrl createShortUrl(String originalUrl) {
    ShortUrl shortUrl = new ShortUrl();
    shortUrl.setOriginalUrl(originalUrl);
    shortUrl.setUsername(userContextService.getCurrentUsername());
    shortUrl.setShortCode(generateShortCode());
    ShortUrl savedShortUrl = shortUrlRepository.save(shortUrl);
    return shortUrlMapper.toCreatedShortUrl(savedShortUrl);
  }

  private String generateShortCode() throws FailedUniqueShortCodeException {
    for (int i = 0; i < surlProperties.maxAttempts(); i++) {
      String shortCode = shortCodeGenerator.generate();
      if (!shortUrlRepository.existsByShortCode(shortCode)) return shortCode;
    }
    throw new FailedUniqueShortCodeException();
  }

  @PreAuthorize("isAuthenticated()")
  public List<CreatedShortUrl> getAllShortUrls() {
    final String currentUsername = userContextService.getCurrentUsername();
    return shortUrlRepository.findAllByUsername(currentUsername).stream()
        .map(shortUrlMapper::toCreatedShortUrl)
        .toList();
  }

  @PreAuthorize("isAuthenticated()")
  public void deleteShortUrl(Long id) {
    ShortUrl shortUrl =
        shortUrlRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ShortUrl not found"));

    if (!shortUrl.getUsername().equals(userContextService.getCurrentUsername())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You are not authorized to delete this ShortUrl");
    }

    shortUrlRepository.deleteById(id);
  }
}
