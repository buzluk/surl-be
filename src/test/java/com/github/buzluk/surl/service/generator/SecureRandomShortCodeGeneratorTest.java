package com.github.buzluk.surl.service.generator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SecureRandomShortCodeGeneratorTest {

  SecureRandomShortCodeGenerator generator = new SecureRandomShortCodeGenerator();

  @Test
  @DisplayName("generate should return a 6-character alphanumeric string")
  void generate() {
    String shortCode = generator.generate();
    assertNotNull(shortCode);
    assertEquals(6, shortCode.length());
    assertTrue(shortCode.matches("^[A-Za-z0-9]{6}$"));
  }
}
