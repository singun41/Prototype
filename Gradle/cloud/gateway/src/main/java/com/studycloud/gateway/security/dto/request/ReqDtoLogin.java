package com.studycloud.gateway.security.dto.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class ReqDtoLogin {
  @NotBlank
  private String userId;

  @NotBlank
  private String userPw;
}
