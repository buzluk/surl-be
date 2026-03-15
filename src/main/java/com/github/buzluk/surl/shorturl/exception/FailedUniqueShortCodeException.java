package com.github.buzluk.surl.shorturl.exception;

public class FailedUniqueShortCodeException extends RuntimeException {
  public FailedUniqueShortCodeException(String message) {
    super(message);
  }

  public FailedUniqueShortCodeException() {
    this("Failed to generate unique short code after multiple attempts");
  }
}
