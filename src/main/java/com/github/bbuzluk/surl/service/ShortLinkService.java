package com.github.bbuzluk.surl.service;

import com.github.bbuzluk.surl.data.entity.ShortLink;
import com.github.bbuzluk.surl.data.model.SurlConfig;
import com.github.bbuzluk.surl.exception.FailedUniqueShortCodeException;
import com.github.bbuzluk.surl.repository.ShortLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShortLinkService {
  private final ShortCodeGenerator shortCodeGenerator;
  private final ShortLinkRepository shortLinkRepository;
  private final UserContextService userContextService;
  private final SurlConfig surlConfig;

  public String createShortCode(String originalUrl) {
    String username = userContextService.getCurrentUsername();
    ShortLink shortLink = ShortLink.create(originalUrl, username, shortCodeGenerator.generate());
    saveWithRetry(shortLink);
    return shortLink.getShortCode();
  }

  private void saveWithRetry(ShortLink shortLink) {
    for (int i = 0; i < surlConfig.maxShortCodeGenerationAttempts(); i++) {
      try {
        shortLinkRepository.save(shortLink);
        return;
      } catch (DataIntegrityViolationException e) {
        shortLink.setShortCode(shortCodeGenerator.generate());
      }
    }
    throw new FailedUniqueShortCodeException();
  }
}
