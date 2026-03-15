package com.github.buzluk.surl.data.model;

import java.time.Duration;

public record SurlConfig(
    String baseUrl, int maxShortCodeGenerationAttempts, Duration authTokenDuration) {}
