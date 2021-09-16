package com.dev.ssms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetDto {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;
    private String confirmPassword;
}
