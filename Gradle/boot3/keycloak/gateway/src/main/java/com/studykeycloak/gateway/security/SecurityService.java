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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {
  private final String urlKeycloak = "http://keycloak-22.local-cubi5.com";
  private final String myRealm = "my-realm";

  private final String urlToken =
  new StringBuilder(urlKeycloak).append("/realms/").append(myRealm).append("/protocol/openid-connect/token").toString();

  private final String grantTypePassword = "password";
  private final String grantTypeRefreshToken = "refresh_token";


  @AllArgsConstructor
  @Getter
  private enum RealmClient {
    TEST_A("test-a", "fbGxsCqsF9UyRf6weYiQh97R68IvJjY9"),
    TEST_B("test-b", "IdcbGx0edECU6ozWskwlRYLfJJn9ZaaJ");

    private String title;
    private String secret;
  }


  public Mono<ResponseEntity<?>> loginClientTestA(ReqDtoLogin dto) throws Exception {
    return login(dto, RealmClient.TEST_A);
  }

  public Mono<ResponseEntity<?>> tokenRefreshClientTestA(ReqDtoRefreshToken token) throws Exception {
    return tokenRefresh(token, RealmClient.TEST_A);
  }



  public Mono<ResponseEntity<?>> loginClientTestB(ReqDtoLogin dto) throws Exception {
    return login(dto, RealmClient.TEST_B);
  }

  public Mono<ResponseEntity<?>> tokenRefreshClientTestB(ReqDtoRefreshToken token) throws Exception {
    return tokenRefresh(token, RealmClient.TEST_B);
  }



  /*
    22.0.3, 22.0.5 두 버전에서 동일한 버그인 것 같은데
    현재 파라미터 username으로 보내면 키클락에서 userId=null 이렇게 메시지가 나오면서 로그인이 되지 않는다.

    vscode thunder-client 확장 프로그램 같은 API 툴로 keycloak의 admin API를 직접 호출할 때는 전혀 문제가 없는데
    아래처럼 WebClient로 보내면 이런 이상한 현상이 발생하고 있다.

    최초 키클락을 테스트할 때는 문제가 없었는데 지금은 왜 발생하는지 이유를 확인하지 못했다.
    그래서 userId 파라미터를 추가해서 보내야 로그인이 된다.
  */
  public Mono<ResponseEntity<?>> login(ReqDtoLogin dto, RealmClient client) throws Exception {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("client_id", client.getTitle());
    formData.add("client_secret", client.getSecret());
    formData.add("grant_type", grantTypePassword);
    formData.add("username", dto.getUserId());
    formData.add("userId", dto.getUserId());
    formData.add("password", dto.getUserPw());

    log.info("username=[{}] userId=[{}]", dto.getUserId(), dto.getUserId());
    return post(formData);
  }

  public Mono<ResponseEntity<?>> tokenRefresh(ReqDtoRefreshToken token, RealmClient client) throws Exception {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("client_id", client.getTitle());
    formData.add("client_secret", client.getSecret());
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
