package com.studykeycloak.user.service;

import java.util.ArrayList;
// import java.util.Arrays;
import java.util.Collections;
import java.util.List;
// import java.util.Map;
import java.util.UUID;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
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
import com.studykeycloak.user.dto.request.ReqDtoRole;
import com.studykeycloak.user.dto.request.ReqDtoUpdate;

// import jakarta.ws.rs.client.Client;
// import jakarta.ws.rs.client.ClientBuilder;
// import jakarta.ws.rs.client.Entity;
// import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
// import jakarta.ws.rs.core.UriBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceKeycloak {
  private final Keycloak keycloak;


  private enum RoleUpdateType {
    ADD, REMOVE;
  }

  @AllArgsConstructor
  @Getter
  public enum RealmClient {
    TEST_A("test-a"), TEST_B("test-b");

    private String title;
  }
  
  
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
    Response res = usersResource.create(setAttribute(user));

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

    return ResponseEntity.ok().build();
  }


  public ResponseEntity<?> addRoleUser(ReqDtoRole dto, RealmClient client) {
    return addRole(dto, ConfigKeycloak.ClientRoles.USER, client);
  }
  public ResponseEntity<?> removeRoleUser(ReqDtoRole dto, RealmClient client) {
    return removeRole(dto, ConfigKeycloak.ClientRoles.USER, client);
  }


  public ResponseEntity<?> addRoleManager(ReqDtoRole dto, RealmClient client) {
    return addRole(dto, ConfigKeycloak.ClientRoles.MANAGER, client);
  }
  public ResponseEntity<?> removeRoleManager(ReqDtoRole dto, RealmClient client) {
    return removeRole(dto, ConfigKeycloak.ClientRoles.MANAGER, client);
  }


  public ResponseEntity<?> addRoleDirector(ReqDtoRole dto, RealmClient client) {
    return addRole(dto, ConfigKeycloak.ClientRoles.DIRECTOR, client);
  }
  public ResponseEntity<?> removeRoleDirector(ReqDtoRole dto, RealmClient client) {
    return removeRole(dto, ConfigKeycloak.ClientRoles.DIRECTOR, client);
  }


  public ResponseEntity<?> addRoleAdmin(ReqDtoRole dto, RealmClient client) {
    return addRole(dto, ConfigKeycloak.ClientRoles.ADMIN, client);
  }
  public ResponseEntity<?> removeRoleAdmin(ReqDtoRole dto, RealmClient client) {
    return removeRole(dto, ConfigKeycloak.ClientRoles.ADMIN, client);
  }


  private ResponseEntity<?> addRole(ReqDtoRole dto, ConfigKeycloak.ClientRoles role, RealmClient client) {
    List<UserRepresentation> userRepresentations = keycloak.realms().realm(ConfigKeycloak.MY_REALM).users().search(dto.getUsername());
    if(userRepresentations.size() > 0) {
      UserRepresentation user = userRepresentations.get(0);
      return updateRole(RoleUpdateType.ADD, user, role, client);
    
    } else {
      return ResponseEntity.badRequest().body("User not exists.");
    }
  }


  private ResponseEntity<?> removeRole(ReqDtoRole dto, ConfigKeycloak.ClientRoles role, RealmClient client) {
    List<UserRepresentation> userRepresentations = keycloak.realms().realm(ConfigKeycloak.MY_REALM).users().search(dto.getUsername());
    if(userRepresentations.size() > 0) {
      UserRepresentation user = userRepresentations.get(0);
      return updateRole(RoleUpdateType.REMOVE, user, role, client);
    
    } else {
      return ResponseEntity.badRequest().body("User not exists.");
    }
  }


  private ResponseEntity<?> updateRole(RoleUpdateType type, UserRepresentation user, ConfigKeycloak.ClientRoles role, RealmClient client) {
    if(user == null)
      return ResponseEntity.badRequest().body("User not exists.");

    RealmResource realmResource = keycloak.realm(ConfigKeycloak.MY_REALM);
    UserResource userResource = realmResource.users().get(user.getId());

    ClientRepresentation clientRepresentation =
    realmResource.clients().findAll().stream().filter(e -> e.getClientId().equals(client.getTitle())).findAny().get();

    RolesResource rolesResource = realmResource.clients().get(clientRepresentation.getId()).roles();
    RoleScopeResource roleScopeResource = userResource.roles().clientLevel(clientRepresentation.getId());

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
  }


  public ResponseEntity<?> updateUserInfo(ReqDtoUpdate dto) {
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
      userResource.update(setAttribute(user));

      log.info("username=[{}] password updated.", user.getUsername());

      // resertPassword 메서드가 의미가 없다..
      // userResource.resetPassword(password);

      return ResponseEntity.ok().build();

    } else {
      return ResponseEntity.badRequest().body("User not exists.");
    }
  }


  private UserRepresentation setAttribute(UserRepresentation user) {
    // user 속성값 추가 테스트
    String attributeUuidStr = UUID.randomUUID().toString();
    log.info("attributeUuidStr: {}", attributeUuidStr);
    return user.singleAttribute("key", attributeUuidStr);
  }
}
