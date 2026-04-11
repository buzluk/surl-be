package com.github.buzluk.surl;

import com.github.buzluk.surl.data.dto.SurlProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SurlProperties.class)
public class SurlApplication {

  public static void main(String[] args) {
    SpringApplication.run(SurlApplication.class, args);
  }
}
