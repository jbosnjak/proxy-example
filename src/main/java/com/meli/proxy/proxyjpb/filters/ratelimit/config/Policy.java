package com.meli.proxy.proxyjpb.filters.ratelimit.config;

import java.util.ArrayList;
import java.util.List;

/**
 * A policy is used to define rate limit constraints within RateLimiter.
 * implementations
 *
 * @author jbosnjak
 */
public class Policy {
  private Long refreshInterval = 60L;
  private Long limit;
  private List<Type> type = new ArrayList<>();

  public enum Type {
    ORIGIN,
    USER_AGENT
  }

  /**
   * getRefreshInterval.
   *
   * @return the refreshInterval
   */
  public Long getRefreshInterval() {
    return refreshInterval;
  }

  /**
   * setRefreshInterval.
   *
   * @param refreshInterval
   *        the refreshInterval to set
   */
  public void setRefreshInterval(Long refreshInterval) {
    this.refreshInterval = refreshInterval;
  }

  /**
   * getLimit.
   *
   * @return the limit
   */
  public Long getLimit() {
    return limit;
  }

  /**
   * setLimit.
   *
   * @param limit
   *        the limit to set
   */
  public void setLimit(Long limit) {
    this.limit = limit;
  }

  /**
   * getType.
   *
   * @return the type
   */
  public List<Type> getType() {
    return type;
  }

}
