package com.github.buzluk.surl.service;

import com.github.buzluk.surl.data.entity.ShortUrl;
import com.github.buzluk.surl.data.entity.UrlClick;
import com.github.buzluk.surl.data.repository.UrlClickRepository;
import com.github.buzluk.surl.util.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlClickService {

  private final UrlClickRepository urlClickRepository;

  public void recordClick(HttpServletRequest request, ShortUrl shortUrl) {
    String ipAddress = RequestUtils.getClientIp(request);
    String userAgent = request.getHeader(HttpHeaders.USER_AGENT);

    UrlClick click = new UrlClick();
    click.setShortUrl(shortUrl);
    click.setIpAddress(ipAddress);
    click.setUserAgent(userAgent);
    urlClickRepository.save(click);
  }
}
