package com.github.buzluk.surl.controller;

import com.github.buzluk.surl.data.entity.ShortUrl;
import com.github.buzluk.surl.service.ShortUrlService;
import com.github.buzluk.surl.service.UrlClickService;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedirectController {

  private final ShortUrlService shortUrlService;
  private final UrlClickService urlClickService;

  @GetMapping("/{shortCode}")
  public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
    ShortUrl shortUrl = shortUrlService.getShortUrlEntity(shortCode);
    urlClickService.recordClick(request, shortUrl);
    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(shortUrl.getOriginalUrl()))
        .build();
  }
}
