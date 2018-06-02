package com.meli.proxy.proxyjpb.config;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

/**
 * This class checks if redis is up and running. If not tries to reconnect every 10 seconds.
 * Gives also information about redis health to another services.
 * 
 * @author jbosnjak
 *
 */
public class RedisHealthChecker {

  private static final Logger log = LoggerFactory.getLogger(RedisHealthChecker.class);
  private final long refrehTime = 10;

  @Autowired
  private HealthIndicator redisHealthIndicator;

  private boolean redisStatus = false;

  public boolean isRedisUp() {
    return redisStatus;
  }

  private void setRedisStatus(boolean redisStatus) {
    this.redisStatus = redisStatus;
  }

  @PostConstruct
  protected void initializeRedisChecker() {
    if (log.isDebugEnabled()) {
      log.debug("RedisHealthChecker.initializeRedisChecker with refresh of " + refrehTime + " seconds");
    }
    new Timer().scheduleAtFixedRate(new RedisCheck(), new Date(), refrehTime * 1000);
  }

  private class RedisCheck extends TimerTask {
    @Override
    public void run() {
      log.trace("RedisCheck.check");
      if (redisHealthIndicator.health().getStatus().equals(Status.UP)) {
        if (!isRedisUp()) {
          log.warn("RedisCheck. Redis is back to ONLINE status...");
        }
        setRedisStatus(true);
      } else {
        if (isRedisUp()) {
          log.warn("RedisCheck. Redis is not reacheable. Switching to contingency.");
        } else {
          log.warn("RedisCheck. Redis is still not reacheable!!! Pls check redis process");
        }
        setRedisStatus(false);
      }
    }
  }
}
