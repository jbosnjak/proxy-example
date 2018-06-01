package com.meli.proxy.proxyjpb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import com.meli.proxy.proxyjpb.config.RedisHealthChecker;

@SpringBootApplication
@EnableZuulProxy
public class ProxyJpbApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyJpbApplication.class, args);
	}
	
  @Bean
  public RedisHealthChecker redisHealthChecker() {
    return new RedisHealthChecker();
  }
}
