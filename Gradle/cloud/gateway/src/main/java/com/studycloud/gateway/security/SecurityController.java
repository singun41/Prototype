package com.studycloud.gateway.security;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.studycloud.gateway.common.ConfigProperties;
import com.studycloud.gateway.security.dto.request.ReqDtoLogin;
import com.studycloud.gateway.security.dto.response.ResDtoToken;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class SecurityController {
  private final SecurityService securityService;


  @PostMapping(ConfigProperties.URL_LOGIN)
  public Mono<ResponseEntity<ResDtoToken>> login(@Validated @RequestBody ReqDtoLogin dto) throws Exception {
    return securityService.login(dto.getUserId(), dto.getUserPw());
  }


  @GetMapping(ConfigProperties.URL_NEW_ACCESS_TOKEN)
  public Mono<ResponseEntity<String>> getNewAccessToken(@RequestParam String token) {
    return securityService.getNewAccessToken(token);
  }


  @GetMapping(ConfigProperties.URL_NEW_REFRESH_TOKEN)
  public Mono<ResponseEntity<String>> getNewRefreshToken(@RequestParam String token) {
    return securityService.getNewRefreshToken(token);
  }


  @GetMapping(ConfigProperties.URL_TOKEN_EXPIRATION)
  public Mono<ResponseEntity<String>> getTokenExpirationDatetime(@RequestParam String token) {
    return securityService.getTokenExpirationDatetime(token);
  }
}
