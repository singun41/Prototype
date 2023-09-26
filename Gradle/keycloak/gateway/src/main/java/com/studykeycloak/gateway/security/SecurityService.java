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
  private final String urlToken = new StringBuilder(urlKeycloak).append("/realms/").append(keycloakRealmName).append("/protocol/openid-connect/token").toString();
  private final String clientId = "dev";
  private final String clientSecret = "PJOkYn6dG73CP7BW2lYW3DOWrM67vQwO";
  private final String grantTypePassword = "password";
  private final String grantTypeRefreshToken = "refresh_token";


  public Mono<ResponseEntity<?>> login(ReqDtoLogin dto) throws Exception {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("client_id", clientId);
    formData.add("client_secret", clientSecret);
    formData.add("grant_type", grantTypePassword);
    formData.add("username", dto.getUserId());
    formData.add("password", dto.getUserPw());

    log.info("userId=[{}]", dto.getUserId());

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
