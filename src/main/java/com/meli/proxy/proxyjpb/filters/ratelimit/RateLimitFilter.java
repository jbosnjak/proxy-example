package com.meli.proxy.proxyjpb.filters.ratelimit;

import static com.meli.proxy.proxyjpb.filters.ratelimit.config.Policy.Type.ORIGIN;
import static com.meli.proxy.proxyjpb.filters.ratelimit.config.Policy.Type.USER_AGENT;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.meli.proxy.proxyjpb.config.RedisHealthChecker;
import com.meli.proxy.proxyjpb.filters.AbstractProxyFilter;
import com.meli.proxy.proxyjpb.filters.ratelimit.config.Policy;
import com.meli.proxy.proxyjpb.filters.ratelimit.config.Policy.Type;
import com.meli.proxy.proxyjpb.filters.ratelimit.config.Rate;
import com.meli.proxy.proxyjpb.filters.ratelimit.config.RateLimitProperties;
import com.meli.proxy.proxyjpb.filters.ratelimit.config.RateLimiter;
import com.netflix.zuul.context.RequestContext;

/**
 * RateLimitFilter.
 *
 * @author jbosnjak
 */
public class RateLimitFilter extends AbstractProxyFilter {

  private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);
  private final RateLimiter limiter;
  private final RateLimitProperties properties;

  @Autowired
  private RedisHealthChecker redisHealthChecker;

  @Autowired
  private CounterService counterService;

  /**
   * RateLimitFilter.
   *
   * @param limiter RateLimiter
   * @param properties RateLimitProperties
   * @param routeLocator RouteLocator
   */
  public RateLimitFilter(RateLimiter limiter, RateLimitProperties properties, RouteLocator routeLocator) {
    this.limiter = limiter;
    this.properties = properties;
    this.setRouteLocator(routeLocator);
    this.setName("RateLimitFilter");
    this.setEnabled(properties.isEnabled());
  }

  @Override
  public String filterType() {
    return "pre";
  }

  @Override
  public int filterOrder() {
    return 1;
  }

  @Override
  public boolean shouldFilter() {
    return this.properties.isEnabled() && policy() != null;
  }

  @Override
  public Object run() {
    log.info("RateLimitFilter.run");
    final RequestContext ctx = RequestContext.getCurrentContext();
    final HttpServletResponse response = ctx.getResponse();
    final HttpServletRequest request = ctx.getRequest();
    Optional.ofNullable(policy()).ifPresent(policy -> {
      final Rate rate;
      if (redisHealthChecker.isRedisUp()) {
        rate = this.limiter.consume(policy, key(request, policy.getType()));

        response.setHeader(Headers.LIMIT, rate.getLimit().toString());
        response.setHeader(Headers.REMAINING, String.valueOf(Math.max(rate.getRemaining(), 0)));
        response.setHeader(Headers.RESET, rate.getReset().toString());
        if (rate.getRemaining() < 0) {
          try {
            ctx.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
            ctx.put("rateLimitExceeded", "true");
            ctx.getResponse().sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate Limit reach");
            ctx.setSendZuulResponse(false);
            counterService.increment("meter."+route().getId() + ".ratelimit.count");
          } catch (IOException e) {
            log.error("Error de respuesta de rateLimit", e);
            throw new RuntimeException(e);
          }
        }
      } else {
        log.info("Redis is Down... skipping rate limit capabilities");
      }
    });
    return null;
  }

  private Policy policy() {
    return (route() != null) ? this.properties.getPolicies().get(route().getId()) : null;
  }

  private String key(final HttpServletRequest request, final List<Type> types) {
    final Route route = route();
    final StringBuilder builder = new StringBuilder(route.getId());
    if (types.contains(ORIGIN)) {
      builder.append(":").append(getRemoteAddr(request));
    }
    if (types.contains(USER_AGENT)) {
      builder.append(":").append(getUserAgent(request));
    }
    return builder.toString();
  }

  private String getUserAgent(final HttpServletRequest request) {

    final String userAgent;
    if (request.getHeader(HttpHeaders.USER_AGENT) != null) {
      userAgent = request.getHeader(HttpHeaders.USER_AGENT);
    } else {
      userAgent = "undefined";
    }
    return userAgent;
  }

  // TODO ver que pasa si esta detrás de un proxy. Quizas haya que considerar agregar alguna validación adicional.
  private String getRemoteAddr(final HttpServletRequest request) {
    String ipAddress = request.getHeader("X-FORWARDED-FOR") != null ? request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr();
    return ipAddress;
  }

  interface Headers {
    String LIMIT = "X-RateLimit-Limit";
    String REMAINING = "X-RateLimit-Remaining";
    String RESET = "X-RateLimit-Reset";
  }
}
