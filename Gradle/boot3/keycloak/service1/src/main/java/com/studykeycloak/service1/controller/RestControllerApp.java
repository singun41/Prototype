package com.studykeycloak.service1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studykeycloak.service1.controller.dto.ReqDto;
import com.studykeycloak.service1.global.ConstantsClientRoles;
import com.studykeycloak.service1.global.RequestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "RestControllerApp", description = "Token authorization test API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class RestControllerApp {
  private final RequestUtils requestUtils;


  @Operation(summary = "all", description = "모두 접근 가능")
  @GetMapping("/all")
  public ResponseEntity<?> accessAll() {
    log.info("endpoint 'all' called.");
    ReqDto dto = ReqDto.builder().message("Hello.").build();
    return ResponseEntity.ok(dto);
  }


  @Operation(summary = "user", description = "유저 권한 접근 가능")
  @GetMapping("/user")
  @PreAuthorize(ConstantsClientRoles.USER)
  public ResponseEntity<?> accessUser() {
    log.info("userId=[{}]", requestUtils.getUserId());

    log.info("endpoint 'user' called.");
    ReqDto dto = ReqDto.builder().message("Hello User.").build();
    return ResponseEntity.ok(dto);
  }


  @Operation(summary = "manager", description = "매니저 권한 접근 가능")
  @GetMapping("/manager")
  @PreAuthorize(ConstantsClientRoles.MANAGER)
  public ResponseEntity<?> accessManager() {
    log.info("userId=[{}]", requestUtils.getUserId());

    log.info("endpoint 'manager' called.");
    ReqDto dto = ReqDto.builder().message("Hello Manager.").build();
    return ResponseEntity.ok(dto);
  }


  @Operation(summary = "admin", description = "어드민 권한 접근 가능")
  @GetMapping("/admin")
  @PreAuthorize(ConstantsClientRoles.ADMIN)
  public ResponseEntity<?> accessAdmin() {
    log.info("userId=[{}]", requestUtils.getUserId());

    log.info("endpoint 'admin' called.");
    ReqDto dto = ReqDto.builder().message("Hello Admin.").build();
    return ResponseEntity.ok(dto);
  }
}
