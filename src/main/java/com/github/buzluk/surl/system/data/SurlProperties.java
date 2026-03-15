package com.github.buzluk.surl.system.data;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

@ConfigurationProperties(prefix = "surl")
public record SurlProperties(
    String baseUrl,
    int maxAttempts,
    @DurationUnit(ChronoUnit.SECONDS) Duration authTokenDuration) {}
