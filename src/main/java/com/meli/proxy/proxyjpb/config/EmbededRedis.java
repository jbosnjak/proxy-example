package com.meli.proxy.proxyjpb.config;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import redis.embedded.RedisServer;

/**
 * Clase para levantar el redis embebido.
 *
 * @author jbosnjak
 *
 */
@Component
@ConditionalOnProperty(name = "spring.redis.embeded.enabled")
public class EmbededRedis {

  private static final Logger log = LoggerFactory.getLogger(EmbededRedis.class);

  @Value("${spring.redis.port}")
  private int redisPort;

  private RedisServer redisServer;

  /**
   * startRedis.
   *
   * @throws IOException IOException
   */
  @PostConstruct
  public void startRedis() throws IOException {
    log.info("Starting embeded Redis");
    redisServer = new RedisServer(redisPort);

    try {
      if (!redisServer.isActive()) {
        redisServer.start();
      }
    } catch (Exception e) {
      log.info("Error al arrancar redis");
    }
  }

  @PreDestroy
  public void stopRedis() {
    log.info("Stopping embeded Redis");
    redisServer.stop();
  }
}
