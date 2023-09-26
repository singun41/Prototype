package com.studykeycloak.service1.common;

public class ConfigProperties {
  public static final String USER_ROLE_ADMIN = "hasAnyRole('admin')";
  public static final String USER_ROLE_MANAGER = "hasAnyRole('manager', 'admin')";
}
