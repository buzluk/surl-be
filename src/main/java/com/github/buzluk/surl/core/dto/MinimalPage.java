package com.github.buzluk.surl.core.dto;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class MinimalPage<T> {

  private final List<T> content;
  private final int currentPage;
  private final int pageSize;
  private final long totalElements;
  private final int totalPages;

  private MinimalPage(Page<T> page) {
    this.content = page.getContent();
    this.currentPage = page.getNumber();
    this.pageSize = page.getSize();
    this.totalElements = page.getTotalElements();
    this.totalPages = page.getTotalPages();
  }

  public static <T> MinimalPage<T> of(Page<T> page) {
    return new MinimalPage<>(page);
  }
}
