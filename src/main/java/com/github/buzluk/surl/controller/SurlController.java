package com.github.buzluk.surl.controller;

import com.github.buzluk.surl.data.dto.VersionInfo;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SurlController {
  private final BuildProperties buildProperties;

  @GetMapping("/version")
  public VersionInfo getVersion() {
    return new VersionInfo(
        buildProperties.getVersion(),
        LocalDate.ofInstant(buildProperties.getTime(), ZoneId.systemDefault()));
  }
}
