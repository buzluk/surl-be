package com.github.bbuzluk.surl.repository;

import com.github.bbuzluk.surl.data.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {}
