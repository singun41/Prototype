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


  @Operation(summary = "유저 추가", description = "유저 추가하는 API")
  @PostMapping
  public ResponseEntity<?> addNewUser(@RequestBody @Validated ReqDtoNewUser dto, BindingResult result) {
    if(result.hasErrors())
      return ResponseEntity.badRequest().body(result.getAllErrors().get(0).getDefaultMessage());

    return serviceUser.addNewUser(dto);
  }


  @Operation(summary = "유저 정보 업데이트")
  @PutMapping
  public ResponseEntity<?> update(@RequestBody ReqDtoUpdate dto) {
    return serviceUser.update(dto);
  }


  @Operation(summary = "권한 추가 - manager")
  @PostMapping("/role/manager")
  public ResponseEntity<?> addRoleManager(@RequestBody ReqDtoUpdate dto) {
    return serviceUser.addRoleManager(dto);
  }

  @Operation(summary = "권한 제거 - manager")
  @DeleteMapping("/role/manager")
  public ResponseEntity<?> removeRoleManager(@RequestBody ReqDtoUpdate dto) {
    return serviceUser.removeRoleManager(dto);
  }


  @Operation(summary = "권한 추가 - director")
  @PostMapping("/role/director")
  public ResponseEntity<?> addRoleDirector(@RequestBody ReqDtoUpdate dto) {
    return serviceUser.addRoleDirector(dto);
  }

  @Operation(summary = "권한 제거 - director")
  @DeleteMapping("/role/director")
  public ResponseEntity<?> removeRoleDirector(@RequestBody ReqDtoUpdate dto) {
    return serviceUser.removeRoleDirector(dto);
  }
}
