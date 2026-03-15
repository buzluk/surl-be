package com.github.buzluk.surl.shorturl.generator;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
class SecureRandomShortCodeGenerator implements ShortCodeGenerator {
  private static final char[] CHARS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
  private static final int SHORT_CODE_LENGTH = 6;
  private static final SecureRandom RANDOM = new SecureRandom();

  public String generate() {
    StringBuilder shortCode = new StringBuilder(SHORT_CODE_LENGTH);
    for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
      int index = RANDOM.nextInt(CHARS.length);
      shortCode.append(CHARS[index]);
    }
    return shortCode.toString();
  }
}
