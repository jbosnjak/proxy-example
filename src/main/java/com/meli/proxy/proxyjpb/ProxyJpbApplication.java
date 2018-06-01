package com.meli.proxy.proxyjpb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import com.meli.proxy.proxyjpb.config.GatewayRibbonConfig;
import com.meli.proxy.proxyjpb.config.RedisHealthChecker;

@SpringBootApplication
@EnableZuulProxy
@ComponentScan(excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = GatewayRibbonConfig.class))
@RibbonClients(defaultConfiguration = GatewayRibbonConfig.class)
public class ProxyJpbApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyJpbApplication.class, args);
	}
	
  @Bean
  public RedisHealthChecker redisHealthChecker() {
    return new RedisHealthChecker();
  }
}
