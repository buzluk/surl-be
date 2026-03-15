package com.github.buzluk.surl.data.mapper;

import com.github.buzluk.surl.data.entity.ShortUrl;
import com.github.buzluk.surl.data.model.SurlConfig;
import com.github.buzluk.surl.data.response.CreatedShortUrl;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ShortUrlMapper {

  @Autowired @Setter protected SurlConfig config;

  @Mapping(target = "fullShortUrl", expression = "java(config.baseUrl() + shortUrl.getShortCode())")
  @Mapping(dateFormat = "dd-MM-YYYY", target = "createdAt", source = "createdAt")
  public abstract CreatedShortUrl toCreatedShortUrl(ShortUrl shortUrl);
}
