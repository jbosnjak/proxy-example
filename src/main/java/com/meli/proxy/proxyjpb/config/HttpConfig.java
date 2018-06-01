package com.meli.proxy.proxyjpb.config;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
public class HttpConfig {

  /**
   * cors filters definition. Basically we let all pass by. :)
   *
   * @return cors filter
   */
  @Bean
  public CorsFilter corsFilter() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    final CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("OPTIONS");
    config.addAllowedMethod("HEAD");
    config.addAllowedMethod("GET");
    config.addAllowedMethod("PUT");
    config.addAllowedMethod("POST");
    config.addAllowedMethod("DELETE");
    config.addAllowedMethod("PATCH");
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

  /**
   * multipartResolver. Added because we skip ApiConfiguration.class autoconfig.
   * 
   * @return MultipartResolver
   */
  @Bean
  public MultipartResolver multipartResolver() {
    return new StandardServletMultipartResolver() {
      private final List<String> allowMultipartMethods = Arrays.asList("put", "post");

      @Override
      public boolean isMultipart(HttpServletRequest request) {
        String method = request.getMethod().toLowerCase();

        if (!allowMultipartMethods.contains(method)) {
          return false;
        }

        String contentType = request.getContentType();
        return (contentType != null && contentType.toLowerCase().startsWith("multipart/"));
      }
    };
  }
  
}
