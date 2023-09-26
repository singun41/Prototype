package com.studycloud.gateway.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.studycloud.gateway.common.ConfigProperties;
import com.studycloud.gateway.security.dto.request.ReqDtoLogin;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class SecurityController {
  private final SecurityService securityService;


  @PostMapping(ConfigProperties.URL_LOGIN)
  public Mono<ResponseEntity<?>> login(@RequestBody ReqDtoLogin dto) throws Exception {
    return securityService.login(dto.getUserId(), dto.getUserPw());
  }


  @GetMapping(ConfigProperties.URL_NEW_ACCESS_TOKEN)
  public Mono<ResponseEntity<?>> getNewAccessToken(@RequestParam String token) {
    return securityService.getNewAccessToken(token);
  }


  @GetMapping(ConfigProperties.URL_NEW_REFRESH_TOKEN)
  public Mono<ResponseEntity<?>> getNewRefreshToken(@RequestParam String token) {
    return securityService.getNewRefreshToken(token);
  }


  @GetMapping(ConfigProperties.URL_TOKEN_EXPIRATION)
  public Mono<ResponseEntity<?>> getTokenExpirationDatetime(@RequestParam String token) {
    return securityService.getTokenExpirationDatetime(token);
  }
}
