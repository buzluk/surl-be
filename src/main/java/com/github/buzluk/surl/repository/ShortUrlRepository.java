package com.github.buzluk.surl.repository;

import com.github.buzluk.surl.data.entity.ShortUrl;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
  List<ShortUrl> findAllByUsername(String username);

  boolean existsByShortCode(String shortCode);
}
