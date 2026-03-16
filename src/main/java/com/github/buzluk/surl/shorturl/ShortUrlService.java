package com.github.buzluk.surl.shorturl;

import com.github.buzluk.surl.core.exception.ServiceException;
import com.github.buzluk.surl.shorturl.data.dto.CreatedShortUrl;
import com.github.buzluk.surl.shorturl.data.entity.ShortUrl;
import com.github.buzluk.surl.shorturl.exception.FailedUniqueShortCodeException;
import com.github.buzluk.surl.shorturl.generator.ShortCodeGenerator;
import com.github.buzluk.surl.system.data.SurlProperties;
import com.github.buzluk.surl.user.service.UserContextService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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
  public Page<CreatedShortUrl> getAllShortUrls(Pageable pageable) {
    final String username = userContextService.getCurrentUsername();
    Page<ShortUrl> shortUrls = shortUrlRepository.findAllByUsername(username, pageable);
    return shortUrls.map(shortUrlMapper::toCreatedShortUrl);
  }

  @PreAuthorize("isAuthenticated()")
  public void deleteShortUrl(Long id) {
    Optional<ShortUrl> shortUrl = shortUrlRepository.findById(id);
    if (shortUrl.isEmpty()) {
      throw new ServiceException("ShortUrl Id:" + id + " not found.");
    }
    if (!userContextService.isActiveUser(shortUrl.get().getUsername())) {
      throw new ServiceException("ShortUrl Id:" + id + " does not belong to the current user.");
    }
    shortUrlRepository.deleteById(id);
  }
}
