package com.github.bbuzluk.surl;

import com.github.bbuzluk.surl.data.model.SurlConfig;
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
      @Value("${surl.max-attempts}") int maxShortCodeGenerationAttempts) {
    return new SurlConfig(baseUrl, maxShortCodeGenerationAttempts);
  }
}
