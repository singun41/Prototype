package com.studycloud.gateway.entity.vo;

import java.util.Arrays;

public enum MemberRole {
  ADMIN("시스템 관리자"), MANAGER("관리자"), USER("사용자");

  
  private String title;
  
  
  private MemberRole(String title) {
    this.title = title;
  }


  public String getTitle() {
    return this.title;
  }


  public static MemberRole enumOf(String title) {
    return Arrays.stream(MemberRole.values()).filter(e -> e.getTitle().equals(title)).findAny().orElse(null);
  }


  public static MemberRole of(String roleStr) {
    return Arrays.stream(MemberRole.values()).filter(e -> e.name().equals(roleStr.toUpperCase())).findAny().orElse(null);
  }
}
