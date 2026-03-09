package com.github.bbuzluk.surl.service;

import com.github.bbuzluk.surl.data.entity.ShortUrl;
import com.github.bbuzluk.surl.data.mapper.ShortUrlMapper;
import com.github.bbuzluk.surl.data.model.SurlConfig;
import com.github.bbuzluk.surl.data.response.CreatedShortUrl;
import com.github.bbuzluk.surl.exception.FailedUniqueShortCodeException;
import com.github.bbuzluk.surl.repository.ShortUrlRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ShortUrlService {
  private final SurlConfig surlConfig;
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
    for (int i = 0; i < surlConfig.maxShortCodeGenerationAttempts(); i++) {
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
