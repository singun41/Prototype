package com.studykeycloak.service1.global;

public class ConstantsClientRoles {
  public static final String ADMIN = "hasAnyRole('admin')";
  public static final String MANAGER = "hasAnyRole('manager')";
  public static final String USER = "hasAnyRole('user')";
}
