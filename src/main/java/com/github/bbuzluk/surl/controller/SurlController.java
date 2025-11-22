package com.github.bbuzluk.surl.controller;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SurlController {
  @GetMapping("/version")
  public Map<Object, Object> getVersion() {
    Map<Object, Object> versionInfo = new HashMap<>();
    versionInfo.put("version", "0.0.0");
    versionInfo.put("buildDate", LocalDate.of(2025, Month.NOVEMBER, 2));
    return versionInfo;
  }
}
