package com.github.bbuzluk.surl.repository;

import com.github.bbuzluk.surl.data.entity.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {}
