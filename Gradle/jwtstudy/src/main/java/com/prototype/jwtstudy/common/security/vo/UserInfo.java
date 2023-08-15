package com.prototype.jwtstudy.common.security.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserInfo {
  private String encodedId;
  private String encodedAuths;
}
