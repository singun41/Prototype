package com.studykeycloak.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReqDtoNewUser {
  @NotBlank
  private String username;

  @NotBlank
  private String password;
}
