package com.studycloud.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.studycloud.gateway.security.vo.UserInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
  private final JwtProvider jwtProvider;


  public JwtAuthenticationFilter(JwtProvider jwtProvider) {
    super(Config.class);
    this.jwtProvider = jwtProvider;
  }

  
  @Override
  public GatewayFilter apply(Config config) {
    return ((exchange, chain) -> {
      ServerHttpRequest req = exchange.getRequest();
      ServerHttpResponse res = exchange.getResponse();

      if(!containsAuthorization(req))
        return handleUnAuthorized(exchange);

      if(!jwtProvider.validation(getToken(req)))
        return handleUnAuthorized(exchange);
      
      if(config.isPreLogger()) {
        log.info("[Req-Id : {}] [Request Path --> {}]", req.getId(), req.getPath());
        if(!req.getQueryParams().isEmpty())
          log.info("[Req-Id : {}] [Quear param --> {}]", req.getId(), req.getQueryParams());
      }
      addAuthorizationHeaders(req);

      return chain.filter(exchange).then(Mono.fromRunnable(() -> {
        if(config.isPostLogger())
          log.info("[Req-Id : {}] [Response Status Code --> {}]", req.getId(), res.getStatusCode());
      }));
    });
  }


  private Mono<Void> handleUnAuthorized(ServerWebExchange exchange) {
    ServerHttpResponse res = exchange.getResponse();
    res.setStatusCode(HttpStatus.UNAUTHORIZED);
    return res.setComplete();
  }

  
  @Getter
  @Setter
  public static class Config {
    private boolean preLogger;
    private boolean postLogger;
  }


  private boolean containsAuthorization(ServerHttpRequest req) {
    return req.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
  }


  private String getToken(ServerHttpRequest req) {
    return req.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0);
  }


  private void addAuthorizationHeaders(ServerHttpRequest req) {
    UserInfo userInfo = jwtProvider.getUserInfo(getToken(req));
    log.info("{}", userInfo);

    req.mutate()
    .header("X-User-Id", userInfo.getId())
    .header("X-User-Role", userInfo.getAuthorities())
    .build();
  }
}
