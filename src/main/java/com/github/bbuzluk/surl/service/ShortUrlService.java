package com.github.bbuzluk.surl.service;

import com.github.bbuzluk.surl.data.entity.ShortUrl;
import com.github.bbuzluk.surl.data.mapper.ShortUrlMapper;
import com.github.bbuzluk.surl.data.model.SurlConfig;
import com.github.bbuzluk.surl.data.response.CreatedShortUrl;
import com.github.bbuzluk.surl.exception.FailedUniqueShortCodeException;
import com.github.bbuzluk.surl.repository.ShortUrlRepository;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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
    shortUrl.setCreatedAt(new Date());
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

  public void deleteShortUrl(Long id) {
    shortUrlRepository.deleteById(id);
  }
}
