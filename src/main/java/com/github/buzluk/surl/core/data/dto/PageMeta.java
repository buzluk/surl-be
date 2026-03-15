package com.github.buzluk.surl.core.data.dto;

public record PageMeta(int page, int size, long totalElements) {
  public int totalPages() {
    return (int) Math.ceil((double) totalElements / size);
  }
}
