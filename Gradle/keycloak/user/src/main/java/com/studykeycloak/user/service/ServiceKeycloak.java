package com.studykeycloak.user.service;

import java.util.ArrayList;
// import java.util.Arrays;
import java.util.Collections;
import java.util.List;
// import java.util.Map;

import org.keycloak.admin.client.Keycloak;
// import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

// import com.google.gson.Gson;
import com.studykeycloak.user.config.ConfigKeycloak;
import com.studykeycloak.user.dto.request.ReqDtoNewUser;
import com.studykeycloak.user.dto.request.ReqDtoUpdate;

// import jakarta.ws.rs.client.Client;
// import jakarta.ws.rs.client.ClientBuilder;
// import jakarta.ws.rs.client.Entity;
// import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
// import jakarta.ws.rs.core.UriBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceKeycloak {
  private final Keycloak keycloak;
  
  
  public ResponseEntity<?> addNewUser(ReqDtoNewUser dto) {
    /*
      버전 22.0.3, 22.0.5 테스트 완료.
      
      유저 등록시 클라이언트의 role을 지정해서 생성할 수 없다.
      그래서 먼저 유저의 기본정보를 등록하고 바로 이어서 유저의 role을 client의 role로 업데이트한다.
      client role은 미리 등록되어 있어야 한다.
    */

    // 유저 계정 생성
    UserRepresentation user = new UserRepresentation();
    user.setUsername(dto.getUsername());
    user.setEnabled(true);

    // 패스워드 설정
    CredentialRepresentation password = new CredentialRepresentation();
    password.setType(CredentialRepresentation.PASSWORD);
    password.setValue(dto.getPassword());
    password.setTemporary(false);
    user.setCredentials(Collections.singletonList(password));


    /*
      같은 username(user의 id)가 있는 경우 409, {"errorMessage":"User exists with same username"}
      성공할 경우 200 OK
    */
    UsersResource usersResource = keycloak.realms().realm(ConfigKeycloak.MY_REALM).users();
    Response res = usersResource.create(user);

    List<String> userUuidValues = new ArrayList<>();
    if(HttpStatus.valueOf(res.getStatus()).is2xxSuccessful()) {
      res.getHeaders().forEach((k, v) -> {
        log.info("header=[{}], value={}", k, v);
        if(k.equalsIgnoreCase("location")) { userUuidValues.add((String)v.get(0)); }
      }
    );

    } else {
      return ResponseEntity.status(res.getStatus()).body(res.readEntity(String.class));
    }

    String userUuid = userUuidValues.get(0).replace(
      new StringBuilder(ConfigKeycloak.URL_SERVER).append("/admin/realms/").append(ConfigKeycloak.MY_REALM).append("/users/").toString()
      , ""
    );

    // 유저 생성 완료.
    UserResource userResource = usersResource.get(userUuid);
    UserRepresentation addedUser = userResource.toRepresentation();
    log.info("username=[{}] added. --> userUuid=[{}]", addedUser.getUsername(), userUuid);

    // 유저의 client_role 할당하고 성공 메시지 리턴.
    return addRoleDefault(addedUser);
  }


  private ResponseEntity<?> addRoleDefault(UserRepresentation user) {
    return updateRole(RoleUpdateType.ADD, user, ConfigKeycloak.PreparedRole.USER);
  }


  public ResponseEntity<?> addRoleManager(ReqDtoUpdate dto) {
    return addRole(dto, ConfigKeycloak.PreparedRole.MANAGER);
  }
  public ResponseEntity<?> removeRoleManager(ReqDtoUpdate dto) {
    return removeRole(dto, ConfigKeycloak.PreparedRole.MANAGER);
  }


  public ResponseEntity<?> addRoleDirector(ReqDtoUpdate dto) {
    return addRole(dto, ConfigKeycloak.PreparedRole.DIRECTOR);
  }
  public ResponseEntity<?> removeRoleDirector(ReqDtoUpdate dto) {
    return removeRole(dto, ConfigKeycloak.PreparedRole.DIRECTOR);
  }


  public ResponseEntity<?> addRoleAdmin(ReqDtoUpdate dto) {
    return addRole(dto, ConfigKeycloak.PreparedRole.ADMIN);
  }
  public ResponseEntity<?> removeRoleAdmin(ReqDtoUpdate dto) {
    return removeRole(dto, ConfigKeycloak.PreparedRole.ADMIN);
  }


  private ResponseEntity<?> addRole(ReqDtoUpdate dto, ConfigKeycloak.PreparedRole role) {
    List<UserRepresentation> userRepresentations = keycloak.realms().realm(ConfigKeycloak.MY_REALM).users().search(dto.getUsername());
    if(userRepresentations.size() > 0) {
      UserRepresentation user = userRepresentations.get(0);
      return updateRole(RoleUpdateType.ADD, user, role);
    
    } else {
      return ResponseEntity.badRequest().body("User not exists.");
    }
  }


  private ResponseEntity<?> removeRole(ReqDtoUpdate dto, ConfigKeycloak.PreparedRole role) {
    List<UserRepresentation> userRepresentations = keycloak.realms().realm(ConfigKeycloak.MY_REALM).users().search(dto.getUsername());
    if(userRepresentations.size() > 0) {
      UserRepresentation user = userRepresentations.get(0);
      return updateRole(RoleUpdateType.REMOVE, user, role);
    
    } else {
      return ResponseEntity.badRequest().body("User not exists.");
    }
  }


  private ResponseEntity<?> updateRole(RoleUpdateType type, UserRepresentation user, ConfigKeycloak.PreparedRole role) {
    if(user == null)
      return ResponseEntity.badRequest().body("User not exists.");

    RealmResource realmResource = keycloak.realm(ConfigKeycloak.MY_REALM);
    UserResource userResource = realmResource.users().get(user.getId());
    RolesResource rolesResource = realmResource.clients().get(ConfigKeycloak.MY_CLIENT_ID).roles();
    RoleScopeResource roleScopeResource = userResource.roles().clientLevel(ConfigKeycloak.MY_CLIENT_ID);

    List<RoleRepresentation> targetRoles = new ArrayList<>();

    rolesResource.list().stream().filter(
      roleRepresentation -> roleRepresentation.getName().equals(role.getTitle())
    ).findAny().ifPresent(
      targetRoles::add
    );

    switch(type) {
      case ADD -> {
        roleScopeResource.add(targetRoles);
        log.info("username=[{}] --> role=[{}] added.", user.getUsername(), role.getTitle());
      }
      case REMOVE -> {
        roleScopeResource.remove(targetRoles);
        log.info("username=[{}] --> role=[{}] removed.", user.getUsername(), role.getTitle());
      }
    }
    return ResponseEntity.ok().build();


    // 아래 코드는 직접 구현 방식
    // String accessToken = "Bearer " + keycloak.tokenManager().getAccessTokenString();
    // List<Map<String, String>> listMap = Arrays.asList(Map.of("id", role.getId(), "name", role.getTitle()));

    // Client client = ClientBuilder.newClient();
    // UriBuilder uriBuilder =
    // UriBuilder.fromPath(ConfigKeycloak.URL_SERVER)
    // .path("/admin/realms/").path(ConfigKeycloak.MY_REALM)
    // .path("/users/").path(user.getId()).path("/role-mappings/clients/").path(ConfigKeycloak.MY_CLIENT_ID);

    // WebTarget target = client.target(uriBuilder);

    // /*
    //   role은 json 배열 형식이어야 한다.

    //   샘플
    //   [
    //     {
    //       "id": "e94a7f56-56ad-4c44-84ca-97444b693a68",
    //       "name": "manager",
    //       "description": "",
    //       "composite": false,
    //       "clientRole": true,
    //       "containerId": "bdbd78da-ac28-4f19-bc78-54ea35a81781"
    //     },
    //     {
    //       "id": "2427bb8d-e4bd-40b3-b7e4-6eacdbadbfe6",
    //       "name": "user",
    //       "description": "",
    //       "composite": false,
    //       "clientRole": true,
    //       "containerId": "bdbd78da-ac28-4f19-bc78-54ea35a81781"
    //     }
    //   ]

    //   id와 name은 필수이고, 나머지는 없어도 된다.
    // */
    
    // Gson gson = new Gson();
    // String clientRoleJsonString = gson.toJson(listMap);
    // Entity<String> entityRoles = Entity.json(clientRoleJsonString);

    // // role update 성공시 204 SUCCESSFUL
    // Response updateRoleResponse = switch(type) {
    //   case ADD -> target.request().header(HttpHeaders.AUTHORIZATION, accessToken).post(entityRoles);
    //   case REMOVE -> target.request().header(HttpHeaders.AUTHORIZATION, accessToken).method(HttpMethod.DELETE.name(), entityRoles);
    // };
    // return ResponseEntity.status(updateRoleResponse.getStatus()).body(updateRoleResponse.readEntity(String.class));
  }


  private enum RoleUpdateType {
    ADD, REMOVE;
  }


  public ResponseEntity<?> update(ReqDtoUpdate dto) {
    UsersResource usersResource = keycloak.realm(ConfigKeycloak.MY_REALM).users();
    List<UserRepresentation> userRepresentations = usersResource.search(dto.getUsername());

    if(userRepresentations.size() > 0) {
      UserRepresentation user = userRepresentations.get(0);
      UserResource userResource = usersResource.get(user.getId());
      
      CredentialRepresentation password = new CredentialRepresentation();
      password.setType(CredentialRepresentation.PASSWORD);
      password.setValue(dto.getPassword());
      password.setTemporary(false);

      user.setEnabled(dto.getEnabled().booleanValue());
      user.setCredentials(Collections.singletonList(password));
      userResource.update(user);

      log.info("username=[{}] password updated.", user.getUsername());

      // resertPassword 메서드가 의미가 없다..
      // userResource.resetPassword(password);

      return ResponseEntity.ok().build();

      
      // 아래 코드는 직접 구현 방식
      // Client client = ClientBuilder.newClient();
      // UriBuilder uriBuilder =
      // UriBuilder.fromPath(ConfigKeycloak.URL_SERVER)
      // .path("/admin/realms/").path(ConfigKeycloak.MY_REALM)
      // .path("/users/").path(user.getId());

      // WebTarget target = client.target(uriBuilder);
      // String accessToken = "Bearer " + keycloak.tokenManager().getAccessTokenString();
      // Gson gson = new Gson();
      // Entity<String> userRepresentationJsonString = Entity.json(gson.toJson(user));

      // Response updateResponse = target.request().header(HttpHeaders.AUTHORIZATION, accessToken).put(userRepresentationJsonString);
      // return ResponseEntity.status(updateResponse.getStatus()).body(updateResponse.readEntity(String.class));

    } else {
      return ResponseEntity.badRequest().body("User not exists.");
    }
  }
}
