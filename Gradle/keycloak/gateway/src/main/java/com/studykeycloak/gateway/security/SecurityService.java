package com.studykeycloak.gateway.security;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.studykeycloak.gateway.security.dto.request.ReqDtoLogin;
import com.studykeycloak.gateway.security.dto.request.ReqDtoRefreshToken;
import com.studykeycloak.gateway.security.dto.response.ResDtoKeycloakToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {
  private final String urlKeycloak = "http://keycloak-2203.local-cubi5.com";
  private final String keycloakRealmName = "keycloak-2203";
  // private final String urlKeycloak = "http://keycloak-1611.local-cubi5.com/auth";
  // private final String keycloakRealmName = "keycloak-1611";

  private final String urlToken = new StringBuilder(urlKeycloak).append("/realms/").append(keycloakRealmName).append("/protocol/openid-connect/token").toString();
  private final String clientId = "dev";
  private final String clientSecret = "MESFQGcFghJS8z0RQV0Ih9yynj8Sfg60";   // keycloak-2203
  // private final String clientSecret = "3g5jQQoRrOyilFOV00Mhq8Ww6wqo8Rmn";   // keycloak-1611
  private final String grantTypePassword = "password";
  private final String grantTypeRefreshToken = "refresh_token";


  public Mono<ResponseEntity<?>> login(ReqDtoLogin dto) throws Exception {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("client_id", clientId);
    formData.add("client_secret", clientSecret);
    formData.add("grant_type", grantTypePassword);
    formData.add("username", dto.getUserId());
    formData.add("userId", dto.getUserId());
    formData.add("password", dto.getUserPw());

    /*
      22.0.3, 22.0.5 두 버전에서 동일한 버그인 것 같은데
      현재 파라미터 username으로 보내면 키클락에서 userId=null 이렇게 메시지가 나오면서 로그인이 되지 않는다.
      thunder-client 확장 프로그램으로 api를 직접 호출할 때는 전혀 문제가 없는데 WebClient로 보내면 이런 이상한 현상이 발생하고 있다.
      최초 키클락을 테스트할 때는 문제가 없었는데 지금은 왜 발생하는지 이유를 확인하지 못했다.
      그래서 userId 파라미터를 추가해서 보내야 로그인이 된다.
    */
    log.info("username and userId=[{}]", dto.getUserId());
    return post(formData);
  }


  public Mono<ResponseEntity<?>> refresh(ReqDtoRefreshToken token) throws Exception {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("client_id", clientId);
    formData.add("client_secret", clientSecret);
    formData.add("grant_type", grantTypeRefreshToken);
    formData.add("refresh_token", token.getValue());

    return post(formData);
  }


  private Mono<ResponseEntity<?>> post(MultiValueMap<String, String> formData) throws Exception {
    return WebClient.create(urlToken)
    .post()
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .body(BodyInserters.fromFormData(formData))
    .retrieve()
    .bodyToMono(ResDtoKeycloakToken.class)
    .flatMap(res -> Mono.just(ResponseEntity.ok(res)));
  }
}
