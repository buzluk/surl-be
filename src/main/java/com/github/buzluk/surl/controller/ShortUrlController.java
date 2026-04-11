package com.github.buzluk.surl.controller;

import com.github.buzluk.surl.data.dto.CreatedShortUrl;
import com.github.buzluk.surl.data.dto.MinimalPage;
import com.github.buzluk.surl.service.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/short-url")
@RequiredArgsConstructor
public class ShortUrlController {

  private final ShortUrlService shortUrlService;

  @PostMapping
  public CreatedShortUrl createShortUrl(@RequestParam("url") String originalUrl) {

    return shortUrlService.createShortUrl(originalUrl);
  }

  @GetMapping
  public MinimalPage<CreatedShortUrl> getAllShortUrls(Pageable pageable) {
    return shortUrlService.getAllShortUrls(pageable);
  }

  @DeleteMapping("/{id}")
  public void deleteShortUrl(@PathVariable("id") Long id) {
    shortUrlService.deleteShortUrl(id);
  }
}
