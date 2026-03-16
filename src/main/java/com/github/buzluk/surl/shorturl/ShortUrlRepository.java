package com.github.buzluk.surl.shorturl;

import com.github.buzluk.surl.shorturl.data.entity.ShortUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
  Page<ShortUrl> findAllByUsername(String username, Pageable pageable);

  boolean existsByShortCode(String shortCode);
}
