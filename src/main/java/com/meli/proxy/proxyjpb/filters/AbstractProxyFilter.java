package com.meli.proxy.proxyjpb.filters;

import java.util.HashMap;

import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.web.util.UrlPathHelper;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * filtro abstract.
 *
 * @author jbosnjak
 *
 */
public abstract class AbstractProxyFilter extends ZuulFilter {

  private String name;
  private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();
  private boolean enabled;
  private RouteLocator routeLocator;

  /**
   * getName.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * setName.
   *
   * @param name
   *        the name to set
   */
  public void setName(String name) {
    this.name = name;
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


  /**
   * getRouteLocator.
   *
   * @return the routeLocator
   */
  public RouteLocator getRouteLocator() {
    return routeLocator;
  }

  public void setRouteLocator(RouteLocator routeLocator) {
    this.routeLocator = routeLocator;
  }

  /**
   * requestURI.
   *
   * @return String
   */
  protected String requestUri() {
    return URL_PATH_HELPER.getPathWithinApplication(RequestContext.getCurrentContext().getRequest());
  }

  /**
   * Return the requestedContext from request.
   *
   * @return The requestedContext
   */
  protected Route route() {
    return this.routeLocator.getMatchingRoute(this.requestUri());
  }

}
