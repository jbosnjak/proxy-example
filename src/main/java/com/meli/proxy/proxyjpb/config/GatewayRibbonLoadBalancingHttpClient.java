package com.meli.proxy.proxyjpb.config;

import java.net.URI;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.netflix.ribbon.apache.RibbonApacheHttpRequest;
import org.springframework.cloud.netflix.ribbon.apache.RibbonApacheHttpResponse;
import org.springframework.cloud.netflix.ribbon.apache.RibbonLoadBalancingHttpClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.client.RequestSpecificRetryHandler;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;

public class GatewayRibbonLoadBalancingHttpClient extends RibbonLoadBalancingHttpClient {
  private static final Logger log = LoggerFactory.getLogger(GatewayRibbonLoadBalancingHttpClient.class);

  public GatewayRibbonLoadBalancingHttpClient(IClientConfig config, ServerIntrospector serverIntrospector) {
    super(config, serverIntrospector);
  }


  @Override
  protected CloseableHttpClient createDelegate(IClientConfig config) {
    return httpClient();
  }

  /**
   * httpClient.
   * 
   * @return HttpClient
   */
  public static CloseableHttpClient httpClient() {
    TrustSelfSignedStrategy trustStrategy = new TrustSelfSignedStrategy();

    SSLContext sslContext = null;
    try {
      sslContext = new SSLContextBuilder().loadTrustMaterial(trustStrategy).build();
    } catch (GeneralSecurityException e) {
      log.error("Error initializing ssl context.", e);
      throw new RuntimeException(e);

    }
    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
    return HttpClientBuilder.create().setSSLSocketFactory(socketFactory).build();
  }

  /**
   * Override to change behavior when a secure ribbon has special caracters. For some reason they did not encode the URI again.
   */
  @Override
  public RibbonApacheHttpResponse execute(RibbonApacheHttpRequest request, final IClientConfig configOverride) throws Exception {
    final RequestConfig.Builder builder = RequestConfig.custom();
    if (configOverride != null) {
      builder.setConnectTimeout(configOverride.get(CommonClientConfigKey.ConnectTimeout, this.connectTimeout));
      builder.setSocketTimeout(configOverride.get(CommonClientConfigKey.ReadTimeout, this.readTimeout));
      builder.setRedirectsEnabled(configOverride.get(CommonClientConfigKey.FollowRedirects, this.followRedirects));
    } else {
      builder.setConnectTimeout(this.connectTimeout);
      builder.setSocketTimeout(this.readTimeout);
      builder.setRedirectsEnabled(this.followRedirects);
    }

    final RequestConfig requestConfig = builder.build();

    if (isSecure(configOverride)) {
      // THE BIG CHANGE is that we call build with a true parameter!!!!
      final URI secureUri = UriComponentsBuilder.fromUri(request.getUri()).scheme("https").build(true).toUri();
      request = request.withNewUri(secureUri);
    }

    final HttpUriRequest httpUriRequest = request.toRequest(requestConfig);
    final HttpResponse httpResponse = this.delegate.execute(httpUriRequest);
    return new RibbonApacheHttpResponse(httpResponse, httpUriRequest.getURI());
  }

  /**
   * Hago un override de este metodo porque en la versi�n CANDEN.SR4 desaparece el retry handler porque se pisaba con el feign client.
   * Pero como nosotros no usamos feign client nos quedamos sin retry handler.
   * Revisar cuando subamos a spring 1.5.0!!!
   */
  @Override
  public RequestSpecificRetryHandler getRequestSpecificRetryHandler(RibbonApacheHttpRequest request, IClientConfig requestConfig) {
    return new RequestSpecificRetryHandler(request.isRetriable(), request.isRetriable(), this.getRetryHandler(), this.config);
  }

}
