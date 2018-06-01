package com.meli.proxy.proxyjpb.filters.ratelimit.config;

/**
 * Represents a view of rate limit in a giving time for a user.
 * limit - How many requests can be executed by the user. Maps to X-RateLimit-Limit header
 * remaining - How many requests are still left on the current window. Maps to X-RateLimit-Remaining header
 * reset - Epoch when the rate is replenished by limit. Maps to X-RateLimit-Reset header
 *
 * @author jbosnjak
 */
public class Rate {
  private Long limit;

  private Long remaining;

  private Long reset;

  /**
   * Rate.
   * 
   * @param limit Long
   * @param remaining Long
   * @param reset Long
   */
  public Rate(Long limit, Long remaining, Long reset) {
    this.limit = limit;
    this.remaining = remaining;
    this.reset = reset;
  }

  public Long getLimit() {
    return limit;
  }

  public Long getRemaining() {
    return remaining;
  }

  public Long getReset() {
    return reset;
  }

}
