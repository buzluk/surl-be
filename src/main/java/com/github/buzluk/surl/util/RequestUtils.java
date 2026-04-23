package com.github.buzluk.surl.util;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestUtils {

  private RequestUtils() {
    // private constructor to prevent instantiation
  }

  public static String getClientIp(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}
