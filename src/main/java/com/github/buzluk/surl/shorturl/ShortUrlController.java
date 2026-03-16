package com.github.buzluk.surl.shorturl;

import com.github.buzluk.surl.shorturl.data.dto.CreatedShortUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
  public Page<CreatedShortUrl> getAllShortUrls(Pageable pageable) {
    return shortUrlService.getAllShortUrls(pageable);
  }

  @DeleteMapping("/{id}")
  public void deleteShortUrl(@PathVariable("id") Long id) {
    shortUrlService.deleteShortUrl(id);
  }
}
