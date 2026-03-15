package com.github.buzluk.surl.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.buzluk.surl.data.enums.ApiError;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    Instant timestamp,
    boolean success,
    T data,
    Map<String, Object> details,
    PageMeta page,
    ApiError error,
    String errorMessage) {

  public static <T> ApiResponse<T> ok(T data, PageMeta page, Map<String, Object> details) {
    return new ApiResponse<>(Instant.now(), true, data, details, page, null, null);
  }

  public static <T> ApiResponse<T> ok(T data, PageMeta page) {
    return ok(data, page, null);
  }

  public static <T> ApiResponse<T> ok(T data) {
    return ok(data, null, null);
  }

  public static <T> ApiResponse<T> ok() {
    return ok(null, null, null);
  }

  public static <T> ApiResponse<T> error(
      ApiError code, String message, Map<String, Object> details) {
    return new ApiResponse<>(Instant.now(), false, null, details, null, code, message);
  }

  public static <T> ApiResponse<T> error(ApiError code, String message) {
    return error(code, message, null);
  }

  public static <T> ApiResponse<T> error(ApiError code) {
    return error(code, null, null);
  }
}
