package com.github.buzluk.surl;

import com.github.buzluk.surl.data.model.SurlConfig;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SurlApplication {

  public static void main(String[] args) {
    SpringApplication.run(SurlApplication.class, args);
  }

  @Bean
  SurlConfig surlConfig(
      @Value("${surl.base-url}") String baseUrl,
      @Value("${surl.max-attempts}") int maxShortCodeGenerationAttempts,
      @Value("${surl.auth-token-duration}") int tokenDuration) {
    return new SurlConfig(
        baseUrl, maxShortCodeGenerationAttempts, Duration.ofSeconds(tokenDuration));
  }
}
