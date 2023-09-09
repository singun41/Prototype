package com.prototype.jwtstudy.presentation.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prototype.jwtstudy.common.security.vo.UserRoles;

@RestController
public class RestControllerMain {
  @GetMapping("/main")   // 로그인이 되어야 접근 가능함.
  public ResponseEntity<?> main() {
    return ResponseEntity.ok("JWT study project.");
  }
  

  @GetMapping("/admin")
  @PreAuthorize(UserRoles.HAS_ADMIN)
  public ResponseEntity<?> adminOnly() {
    return ResponseEntity.ok("admin only");
  }


  @GetMapping("/manager")
  @PreAuthorize(UserRoles.HAS_MANAGER)
  public ResponseEntity<?> managerOnly() {
    return ResponseEntity.ok("manager only");
  }


  @GetMapping("/free")   // 로그인 없이도 접근할 수 있는 url, SecurityConfig에 추가되어 있다.
  public ResponseEntity<?> freeAccess() {
    return ResponseEntity.ok("free accessed url");
  }
}
