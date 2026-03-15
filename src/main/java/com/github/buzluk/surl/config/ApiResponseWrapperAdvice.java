package com.github.buzluk.surl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.buzluk.surl.data.enums.ApiError;
import com.github.buzluk.surl.data.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@ResponseStatus(HttpStatus.OK)
@Slf4j
@RequiredArgsConstructor
class ApiResponseWrapperAdvice implements ResponseBodyAdvice<Object> {
  private final ObjectMapper objectMapper;

  @Override
  public boolean supports(
      MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {
    if (body instanceof ApiResponse) return body;
    if (body instanceof String bodyAsString) {
      Object result = wrapStringBody(bodyAsString);
      response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
      return result;
    }
    return ApiResponse.ok(body);
  }

  private Object wrapStringBody(String body) {
    try {
      return objectMapper.writeValueAsString(ApiResponse.ok(body));
    } catch (Exception e) {
      log.error("Error wrapping String response", e);
      return ApiResponse.error(ApiError.SERVER_ERROR_OCCURED, "Error processing response");
    }
  }

  @ExceptionHandler(Exception.class)
  ApiResponse<Object> handleException(Exception ex) {
    log.error("Unhandled exception occurred", ex);
    return ApiResponse.error(ApiError.SERVER_ERROR_OCCURED, ex.getMessage());
  }

  @ExceptionHandler(AuthenticationException.class)
  ApiResponse<Object> handleAuthenticationException(AuthenticationException ex) {
    log.error("Unhandled authentication exception occurred", ex);
    return ApiResponse.error(ApiError.AUTHENTICATION_FAILED, ex.getMessage());
  }
}
