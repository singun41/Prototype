package com.studykeycloak.user;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

import com.google.gson.Gson;
import com.studykeycloak.user.config.ConfigKeycloak;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class UserApplicationTests {
	@Autowired
  private Keycloak keycloak;
  private final String newUsername = "user1";


	@Test
  public void addUserTest() {
    /*
      유저 등록시 클라이언트의 role을 지정해서 생성할 수 없다.
      그래서 먼저 유저의 기본정보를 등록하고 바로 이어서 유저의 role을 client의 role로 업데이트한다.
      client role은 미리 등록되어 있어야 한다.
    */

    UserRepresentation userRepresentation = new UserRepresentation();

    CredentialRepresentation password = new CredentialRepresentation();
    password.setType(CredentialRepresentation.PASSWORD);
    password.setValue("asdf1234!@");
    password.setTemporary(false);

    List<CredentialRepresentation> credentials = Arrays.asList(password);
    userRepresentation.setUsername(newUsername);
    userRepresentation.setCredentials(credentials);
		userRepresentation.setEnabled(true);
		
    Response response = keycloak.realms().realm(ConfigKeycloak.MY_REALM).users().create(userRepresentation);
    log.info("creation result: {}", response.toString());
    log.info("user's info url: {}", response.getStringHeaders().get(HttpHeaders.LOCATION.toLowerCase()));

    List<UserRepresentation> userRepresentations = keycloak.realms().realm(ConfigKeycloak.MY_REALM).users().search(newUsername);
    if(userRepresentations.size() > 0) {
      UserRepresentation addedUser = userRepresentations.get(0);
      String userId = addedUser.getId();

      String accessToken = keycloak.tokenManager().getAccessTokenString();
      log.info("accessToken: {}", accessToken);

      List<Map<String, String>> listMap = Arrays.asList(
        Map.of("id", ConfigKeycloak.PREPARED_ROLE_USER_ID, "name", ConfigKeycloak.PREPARED_ROLE_USER_NAME),
        Map.of("id", ConfigKeycloak.PREPARED_ROLE_MANAGER_ID, "name", ConfigKeycloak.PREPARED_ROLE_MANAGER_NAME)
      );

      Client client = ClientBuilder.newClient();
      UriBuilder uriBuilder =
      UriBuilder.fromPath(ConfigKeycloak.URL_SERVER)
      .path("/admin/realms/").path(ConfigKeycloak.MY_REALM)
      .path("/users/").path(userId).path("/role-mappings/clients/").path(ConfigKeycloak.MY_CLIENT_ID);

      WebTarget target = client.target(uriBuilder);
      log.info("uri: {}", target.getUri().toString());

      Gson gson = new Gson();
      String clientRoleJsonString = gson.toJson(listMap);
      Entity<String> entityRoles = Entity.json(clientRoleJsonString);
      log.info("entityRoles: {}", clientRoleJsonString);

      Response res = target.request().header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).post(entityRoles);
      log.info("role update result: {} {}", res.getStatus(), res.readEntity(String.class));
    }
  }
}
