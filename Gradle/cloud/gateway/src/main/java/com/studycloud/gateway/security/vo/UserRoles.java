package com.studycloud.gateway.security.vo;

public class UserRoles {
  public static final String ADMIN = "ADMIN";
  public static final String MANAGER = "MANAGER";
  public static final String USER = "USER";

  public static final String HAS_ADMIN = "hasAnyRole('ADMIN')";
  public static final String HAS_MANAGER = "hasAnyRole('MANAGER', 'ADMIN')";   // ADMIN은 전체권한이므로 MANAGER 권한에도 접근 가능.
}
