package com.github.buzluk.surl.data.repository;

import com.github.buzluk.surl.data.entity.ShortUrl;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
  Page<ShortUrl> findAllByUsername(String username, Pageable pageable);

  boolean existsByShortCode(String shortCode);

  Optional<ShortUrl> findByShortCode(String shortCode);
}
