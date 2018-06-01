package com.meli.proxy.proxyjpb.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.netflix.ribbon.apache.RibbonLoadBalancingHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.netflix.client.RetryHandler;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.NoOpPing;
import com.netflix.servo.monitor.Monitors;

@Configuration
public class GatewayRibbonConfig {

  private static final String NAME = "gateway";

  /**
   * ribbonLoadBalancingHttpClient.
   * 
   * @param config IClientConfig
   * @param serverIntrospector ServerIntrospector
   * @param loadBalancer ILoadBalancer
   * @param retryHandler RetryHandler
   * @return RibbonLoadBalancingHttpClient
   */
  @Bean
  @ConditionalOnProperty(name = "ribbon.trustSelfSigned", havingValue = "true", matchIfMissing = true)
  public RibbonLoadBalancingHttpClient ribbonLoadBalancingHttpClient(IClientConfig config, ServerIntrospector serverIntrospector,
      ILoadBalancer loadBalancer, RetryHandler retryHandler) {
    GatewayRibbonLoadBalancingHttpClient client = new GatewayRibbonLoadBalancingHttpClient(config, serverIntrospector);
    client.setLoadBalancer(loadBalancer);
    client.setRetryHandler(retryHandler);
    Monitors.registerObject("Client_" + NAME, client);
    return client;
  }

  @Profile("!test")
  @Bean
  public IPing ribbonPing(IClientConfig config) {
    return new NoOpPing();
  }

  @Profile("!test")
  @Bean
  public IRule ribbonRule(IClientConfig config) {
    return new AvailabilityFilteringRule();
  }
}
