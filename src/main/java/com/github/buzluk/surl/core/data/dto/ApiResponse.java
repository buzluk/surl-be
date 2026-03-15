package com.github.buzluk.surl.core.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    Instant timestamp,
    boolean success,
    T data,
    Map<String, Object> details,
    PageMeta page,
    String errorMessage) {

  public static <T> ApiResponse<T> ok(T data, PageMeta page, Map<String, Object> details) {
    return new ApiResponse<>(Instant.now(), true, data, details, page, null);
  }

  public static <T> ApiResponse<T> ok(T data, PageMeta page) {
    return ok(data, page, null);
  }

  public static <T> ApiResponse<T> ok(T data) {
    return ok(data, null);
  }

  public static <T> ApiResponse<T> ok() {
    return ok(null);
  }

  public static <T> ApiResponse<T> error(String message, Map<String, Object> details) {
    return new ApiResponse<>(Instant.now(), false, null, details, null, message);
  }

  public static <T> ApiResponse<T> error(String message) {
    return error(message, null);
  }
}
