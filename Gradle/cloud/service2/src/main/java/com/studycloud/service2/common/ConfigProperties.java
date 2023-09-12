package com.studycloud.service2.common;

public class ConfigProperties {
  public static final String USER_ROLE_ADMIN = "hasAnyRole('ADMIN')";
  public static final String USER_ROLE_MANAGER = "hasAnyRole('MANAGER', 'ADMIN')";
}
