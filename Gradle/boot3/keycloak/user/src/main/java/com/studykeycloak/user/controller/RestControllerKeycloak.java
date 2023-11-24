package com.studykeycloak.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studykeycloak.user.dto.request.ReqDtoNewUser;
import com.studykeycloak.user.dto.request.ReqDtoRole;
import com.studykeycloak.user.dto.request.ReqDtoUpdate;
import com.studykeycloak.user.service.ServiceKeycloak;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "RestControllerKeycloak", description = "Keycloak Admin API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/keycloak")
public class RestControllerKeycloak {
  private final ServiceKeycloak serviceUser;


  @Operation(summary = "유저 생성", description = "유저 추가하는 API")
  @PostMapping
  public ResponseEntity<?> addNewUser(@RequestBody @Validated ReqDtoNewUser dto, BindingResult result) {
    if(result.hasErrors())
      return ResponseEntity.badRequest().body(result.getAllErrors().get(0).getDefaultMessage());
    return serviceUser.addNewUser(dto);
  }


  @Operation(summary = "유저 정보 업데이트", description = "패스워드 및 계정 사용 여부")
  @PutMapping
  public ResponseEntity<?> updateUserInfo(@RequestBody ReqDtoUpdate dto) {
    return serviceUser.updateUserInfo(dto);
  }





  /* ----- ----- ----- ----- ----- client test-a ----- ----- ----- ----- ----- */
  @Operation(summary = "권한 user 추가 - client test-a")
  @PostMapping("/role/client/test-a/user")
  public ResponseEntity<?> addRoleUserClientTestA(@RequestBody ReqDtoRole dto) {
    return serviceUser.addRoleUser(dto, ServiceKeycloak.RealmClient.TEST_A);
  }

  @Operation(summary = "권한 user 제거 - client test-a")
  @DeleteMapping("/role/client/test-a/user")
  public ResponseEntity<?> removeRoleUserClientTestA(@RequestBody ReqDtoRole dto) {
    return serviceUser.removeRoleUser(dto, ServiceKeycloak.RealmClient.TEST_A);
  }



  @Operation(summary = "권한 manager 추가 - client test-a")
  @PostMapping("/role/client/test-a/manager")
  public ResponseEntity<?> addRoleManagerClientTestA(@RequestBody ReqDtoRole dto) {
    return serviceUser.addRoleManager(dto, ServiceKeycloak.RealmClient.TEST_A);
  }

  @Operation(summary = "권한 manager 제거 - client test-a")
  @DeleteMapping("/role/client/test-a/manager")
  public ResponseEntity<?> removeRoleManagerClientTestA(@RequestBody ReqDtoRole dto) {
    return serviceUser.removeRoleManager(dto, ServiceKeycloak.RealmClient.TEST_A);
  }



  @Operation(summary = "권한 director 추가 - client test-a")
  @PostMapping("/role/client/test-a/director")
  public ResponseEntity<?> addRoleDirectorClientTestA(@RequestBody ReqDtoRole dto) {
    return serviceUser.addRoleDirector(dto, ServiceKeycloak.RealmClient.TEST_A);
  }

  @Operation(summary = "권한 director 제거 - client test-a")
  @DeleteMapping("/role/client/test-a/director")
  public ResponseEntity<?> removeRoleDirectorClientTestA(@RequestBody ReqDtoRole dto) {
    return serviceUser.removeRoleDirector(dto, ServiceKeycloak.RealmClient.TEST_A);
  }
  /* ----- ----- ----- ----- ----- client test-a ----- ----- ----- ----- ----- */





  /* ----- ----- ----- ----- ----- client test-b ----- ----- ----- ----- ----- */
  @Operation(summary = "권한 user 추가 - client test-b")
  @PostMapping("/role/client/test-b/user")
  public ResponseEntity<?> addRoleUserClientTestB(@RequestBody ReqDtoRole dto) {
    return serviceUser.addRoleUser(dto, ServiceKeycloak.RealmClient.TEST_B);
  }

  @Operation(summary = "권한 user 제거 - client test-b")
  @DeleteMapping("/role/client/test-b/user")
  public ResponseEntity<?> removeRoleUserClientTestB(@RequestBody ReqDtoRole dto) {
    return serviceUser.removeRoleUser(dto, ServiceKeycloak.RealmClient.TEST_B);
  }



  @Operation(summary = "권한 manager 추가 - client test-b")
  @PostMapping("/role/client/test-b/manager")
  public ResponseEntity<?> addRoleManagerClientTestB(@RequestBody ReqDtoRole dto) {
    return serviceUser.addRoleManager(dto, ServiceKeycloak.RealmClient.TEST_B);
  }

  @Operation(summary = "권한 manager 제거 - client test-b")
  @DeleteMapping("/role/client/test-b/manager")
  public ResponseEntity<?> removeRoleManagerClientTestB(@RequestBody ReqDtoRole dto) {
    return serviceUser.removeRoleManager(dto, ServiceKeycloak.RealmClient.TEST_B);
  }



  @Operation(summary = "권한 director 추가 - client test-b")
  @PostMapping("/role/client/test-b/director")
  public ResponseEntity<?> addRoleDirectorClientTestB(@RequestBody ReqDtoRole dto) {
    return serviceUser.addRoleDirector(dto, ServiceKeycloak.RealmClient.TEST_B);
  }

  @Operation(summary = "권한 director 제거 - client test-b")
  @DeleteMapping("/role/client/test-b/director")
  public ResponseEntity<?> removeRoleDirectorClientTestB(@RequestBody ReqDtoRole dto) {
    return serviceUser.removeRoleDirector(dto, ServiceKeycloak.RealmClient.TEST_B);
  }
  /* ----- ----- ----- ----- ----- client test-b ----- ----- ----- ----- ----- */
}
