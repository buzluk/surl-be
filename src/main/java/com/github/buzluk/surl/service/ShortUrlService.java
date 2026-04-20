package com.github.buzluk.surl.service;

import com.github.buzluk.surl.data.dto.CreatedShortUrl;
import com.github.buzluk.surl.data.dto.MinimalPage;
import com.github.buzluk.surl.data.dto.SurlProperties;
import com.github.buzluk.surl.data.entity.ShortUrl;
import com.github.buzluk.surl.data.exception.FailedUniqueShortCodeException;
import com.github.buzluk.surl.data.exception.ServiceException;
import com.github.buzluk.surl.data.repository.ShortUrlRepository;
import com.github.buzluk.surl.service.generator.ShortCodeGenerator;
import com.github.buzluk.surl.service.mapper.ShortUrlMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
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

  @PreAuthorize("isAuthenticated()")
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
  public MinimalPage<CreatedShortUrl> getAllShortUrls(Pageable pageable) {
    Pageable adjustedPageable = adjustPageable(pageable);
    final String username = userContextService.getCurrentUsername();
    Page<ShortUrl> shortUrls = shortUrlRepository.findAllByUsername(username, adjustedPageable);
    return MinimalPage.of(shortUrls.map(shortUrlMapper::toCreatedShortUrl));
  }

  private Pageable adjustPageable(Pageable pageable) {
    Order fullShortUrlOrder = pageable.getSort().getOrderFor("fullShortUrl");
    if (fullShortUrlOrder == null) {
      return pageable;
    }
    Sort.Direction direction = fullShortUrlOrder.getDirection();
    var newOrders = List.of(new Order(direction, "originalUrl"), new Order(direction, "shortCode"));
    return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(newOrders));
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

  public String getOriginalUrl(String shortCode) {
    return shortUrlRepository
        .findByShortCode(shortCode)
        .orElseThrow(() -> new ServiceException("Short URL not found: " + shortCode))
        .getOriginalUrl();
  }
}
