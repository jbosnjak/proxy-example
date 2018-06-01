package com.meli.proxy.proxyjpb.filters.ratelimit.config;

/**
 * RateLimiter interface.
 *
 * @author jbosnjak
 */
public interface RateLimiter {

  /**
   * consume.
   * 
   * @param policy
   *        - Template for which rates should be created in case there's no
   *        rate limit associated with the key
   * @param key
   *        - Unique key that identifies a request
   * @return a view of a user's rate request limit
   */
  Rate consume(Policy policy, String key);
}
