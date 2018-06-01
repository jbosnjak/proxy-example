package com.meli.proxy.proxyjpb.filters.ratelimit.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.meli.proxy.proxyjpb.filters.ratelimit.RateLimitFilter;
import com.meli.proxy.proxyjpb.filters.ratelimit.config.redis.RedisRateLimiter;

/**
 * RateLimitAutoConfiguration.
 * 
 * @author Jbosnjak
 */
@Configuration
@EnableConfigurationProperties({ RateLimitProperties.class })
@ConditionalOnProperty(name = "zuul.ratelimit.enabled")
public class RateLimitAutoConfiguration {

  private static final Logger log = LoggerFactory.getLogger(RateLimitAutoConfiguration.class);

  @Bean
  public RateLimitFilter rateLimiterFilter(RateLimiter rateLimiter, RateLimitProperties rateLimitProperties, RouteLocator routeLocator) {
    log.info("RateLimiterFilter is enabled!!! Initializating....");
    return new RateLimitFilter(rateLimiter, rateLimitProperties, routeLocator);
  }

  @ConditionalOnClass(RedisTemplate.class)
  public static class RedisConfiguration {
    @SuppressWarnings("rawtypes")
    @Bean
    public RateLimiter redisRateLimiter(RedisTemplate redisTemplate) {
      return new RedisRateLimiter(redisTemplate);
    }
  }

}
