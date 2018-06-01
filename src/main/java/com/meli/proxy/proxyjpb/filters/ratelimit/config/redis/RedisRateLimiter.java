package com.meli.proxy.proxyjpb.filters.ratelimit.config.redis;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.springframework.data.redis.core.RedisTemplate;

import com.meli.proxy.proxyjpb.filters.ratelimit.config.Policy;
import com.meli.proxy.proxyjpb.filters.ratelimit.config.Rate;
import com.meli.proxy.proxyjpb.filters.ratelimit.config.RateLimiter;

/**
 * RedisRateLimiter.
 *
 * @author jbosnjak
 */
@SuppressWarnings("rawtypes")
public class RedisRateLimiter implements RateLimiter {
  /**
   * RedisTemplate.
   */
  private RedisTemplate template;

  /**
   * RedisRateLimiter.
   *
   * @param template RedisTemplate
   */
  public RedisRateLimiter(RedisTemplate template) {
    this.template = template;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Rate consume(final Policy policy, final String key) {
    final Long limit = policy.getLimit();
    final Long refreshInterval = policy.getRefreshInterval();
    final Long current = this.template.boundValueOps(key).increment(1L);
    Long expire = this.template.getExpire(key);
    if (expire == null || expire == -1) {
      this.template.expire(key, refreshInterval, SECONDS);
      expire = refreshInterval;
    }
    return new Rate(limit, Math.max(-1, limit - current), SECONDS.toMillis(expire));
  }
}
