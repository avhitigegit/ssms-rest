package com.dev.ssms.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDto {
    private String userSeq;
    private String email;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String otherNames;
    private String title;
    private String descriptionPictureUrl;
    private String emailVerifiedCode;
    private String passwordVerifiedCode;
    private Boolean isEmailVerified;
    private Boolean isPasswordReset;
    private LocalDateTime dateOfBirth;
}
