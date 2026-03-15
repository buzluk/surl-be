package com.github.buzluk.surl.shorturl;

import com.github.buzluk.surl.shorturl.data.entity.ShortUrl;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
  List<ShortUrl> findAllByUsername(String username);

  boolean existsByShortCode(String shortCode);
}
