package com.meli.proxy.proxyjpb.filters.ratelimit.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RateLimitProperties.
 *
 * @author jbosnjak
 */
@ConfigurationProperties("zuul.ratelimit")
public class RateLimitProperties {

  private Map<String, Policy> policies = new LinkedHashMap<>();
  private boolean enabled;

  /**
   * getPolicies.
   *
   * @return the policies
   */
  public Map<String, Policy> getPolicies() {
    return policies;
  }


  /**
   * isEnabled.
   *
   * @return the enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * setEnabled.
   *
   * @param enabled
   *        the enabled to set
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

}
