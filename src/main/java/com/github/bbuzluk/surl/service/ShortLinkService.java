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
    String shortCode = shortCodeGenerator.generate();
    ShortLink shortLink = ShortLink.create(originalUrl, username, shortCode);
    saveWithRetry(shortLink);
    return shortLink.getShortCode();
  }

  private void saveWithRetry(ShortLink shortLink) {
    for (int i = 0; i < surlConfig.maxShortCodeGenerationAttempts(); i++) {
      if (tryToSaveShortLink(shortLink)) return;
    }
    throw new FailedUniqueShortCodeException();
  }

  private boolean tryToSaveShortLink(ShortLink shortLink) {
    try {
      shortLinkRepository.save(shortLink);
      return true;
    } catch (DataIntegrityViolationException e) {
      String newShortCode = shortCodeGenerator.generate();
      shortLink.setShortCode(newShortCode);
      return false;
    }
  }
}
