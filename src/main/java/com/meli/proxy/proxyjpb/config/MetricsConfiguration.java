package com.meli.proxy.proxyjpb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.endpoint.MetricsEndpointMetricReader;
import org.springframework.boot.actuate.metrics.statsd.StatsdMetricWriter;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class MetricsConfiguration {

  private static final Logger log = LoggerFactory.getLogger(MetricsConfiguration.class);

  @Value("${spring.application.name}")
  private String prefix;

  @Value("${spring.metrics.export.statsd.host}")
  private String host = "localhost";

  @Value("${spring.metrics.export.statsd.port}")
  private int port = 8125;

  /*
   * Reading all metrics that appear on the /metrics endpoint to expose them to metrics writer beans.
   */
  @Bean
  public MetricsEndpointMetricReader metricsEndpointMetricReader(final MetricsEndpoint metricsEndpoint) {
    return new MetricsEndpointMetricReader(metricsEndpoint);
  }


  /**
   * Metrics exporter that sends upd packages to telegraf!!.
   * 
   * @return metrics writer
   */
  @Bean
  @ExportMetricWriter
  @Primary
  public MetricWriter metricWriter() {
    log.info("Configuring StatsdMetricWriter to export with prefix {} to {}:{}", prefix, host, port);
    return new StatsdMetricWriter(prefix, host, port);
  }
}
