package com.studykeycloak.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReqDtoUpdate {
  @NotBlank
  private String username;

  @NotNull
  private Boolean enabled;

  @NotBlank
  private String password;
}
