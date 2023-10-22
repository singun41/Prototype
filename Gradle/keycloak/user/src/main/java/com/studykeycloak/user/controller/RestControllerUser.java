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
import com.studykeycloak.user.service.ServiceUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class RestControllerUser {
  private final ServiceUser serviceUser;


  @PostMapping
  public ResponseEntity<?> addNewUser(@RequestBody @Validated ReqDtoNewUser dto, BindingResult result) {
    if(result.hasErrors())
      return ResponseEntity.badRequest().body(result.getAllErrors().get(0).getDefaultMessage());

    return serviceUser.addNew(dto);
  }


  @PostMapping("/role/manager")
  public ResponseEntity<?> addRoleManager(@RequestBody ReqDtoUpdate dto) {
    return serviceUser.addRoleManager(dto);
  }


  @DeleteMapping("/role/manager")
  public ResponseEntity<?> removeRoleManager(@RequestBody ReqDtoUpdate dto) {
    return serviceUser.removeRoleManager(dto);
  }


  @PutMapping
  public ResponseEntity<?> update(@RequestBody ReqDtoUpdate dto) {
    return serviceUser.update(dto);
  }
}
