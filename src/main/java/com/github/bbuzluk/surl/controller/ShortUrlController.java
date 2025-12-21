package com.github.bbuzluk.surl.controller;

import com.github.bbuzluk.surl.data.response.CreatedShortUrl;
import com.github.bbuzluk.surl.service.ShortUrlService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
  public List<CreatedShortUrl> getAllShortUrls() {
    return shortUrlService.getAllShortUrls();
  }

  @DeleteMapping("/{id}")
  public void deleteShortUrl(@PathVariable("id") Long id) {
    shortUrlService.deleteShortUrl(id);
  }
}
