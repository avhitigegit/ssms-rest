package com.dev.ssms.model;

import com.dev.ssms.component.enums.Status;
import com.dev.ssms.component.util.UtilService;
import com.dev.ssms.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user")
public class User extends SharedModule {

    @Id
    @Column(name = "user_seq")
    private String userSeq;
    @Column(name = "email")
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "other_name")
    private String otherNames;
    @Column(name = "title")
    private String title;
    @Column(name = "dp_url")
    private String descriptionPictureUrl;
    @Column(name = "email_verified_code")
    private String emailVerifiedCode;
    @Column(name = "password_verified_code")
    private String passwordVerifiedCode;
    @Column(name = "is_email_verified")
    private Boolean isEmailVerified;
    @Column(name = "is_password_reset")
    private Boolean isPasswordReset;
    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;
    @Column(name = "role_seq")
    private Integer roleSeq;


    public static UserDto setUserInRegistration(User dbUser, UserDto userDto) {
        userDto.setUserSeq(dbUser.getUserSeq());
        userDto.setEmail(dbUser.getEmail());
//        userDto.setPassword(dbUser.getPassword());
        userDto.setEmailVerifiedCode(dbUser.getEmailVerifiedCode());
        userDto.setPasswordVerifiedCode(dbUser.getPasswordVerifiedCode());
        userDto.setIsEmailVerified(dbUser.getIsEmailVerified());
        userDto.setIsPasswordReset(dbUser.getIsPasswordReset());
//        userDto.setStatusSeq(dbUser.getStatusSeq());
//        userDto.setRoleSeq(dbUser.getRoleSeq());
//        userDto.setCreatedAt(dbUser.getCreatedAt());
//        userDto.setUpdatedAt(dbUser.getUpdatedAt());

//        userDto.setFirstName(dbUser.getFirstName());
//        userDto.setMiddleName(dbUser.getMiddleName());
//        userDto.setLastName(dbUser.getLastName());
//        userDto.setOtherNames(dbUser.getOtherNames());
//        userDto.setTitle(dbUser.getTitle());
//        userDto.setDescriptionPictureUrl(dbUser.getDescriptionPictureUrl());
//        userDto.setDateOfBirth(dbUser.getDateOfBirth());

        return userDto;
    }

    public static User initFrom(User user) {
        user.setUserSeq("USER-" + UtilService.generateRandomString());
        user.setStatus(Status.PENDING.getStatusSeq());
        user.setEmailVerifiedCode(UtilService.generateRandomString());
        user.setIsEmailVerified(false);
        return user;
    }

}
