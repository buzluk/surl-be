package com.github.buzluk.surl.service.mapper;

import com.github.buzluk.surl.data.dto.CreatedShortUrl;
import com.github.buzluk.surl.data.dto.SurlProperties;
import com.github.buzluk.surl.data.entity.ShortUrl;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ShortUrlMapper {

  @Autowired @Setter protected SurlProperties properties;

  @Mapping(
      target = "fullShortUrl",
      expression = "java(properties.baseUrl() + shortUrl.getShortCode())")
  @Mapping(dateFormat = "dd-MM-YYYY", target = "createdAt", source = "createdAt")
  public abstract CreatedShortUrl toCreatedShortUrl(ShortUrl shortUrl);
}
