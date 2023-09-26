package com.studycloud.gateway.security.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class ReqDtoLogin {
  private String userId;
  private String userPw;
}
