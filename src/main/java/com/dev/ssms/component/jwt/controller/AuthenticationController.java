package com.dev.ssms.component.jwt.controller;

import com.dev.ssms.component.constant.CurrentUser;
import com.dev.ssms.component.jwt.model.SignUpDto;
import com.dev.ssms.component.jwt.service.AuthenticationService;
import com.dev.ssms.dto.PasswordResetDto;
import com.dev.ssms.dto.UserDto;
import com.dev.ssms.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthenticationController {
    private static final Logger LOGGER = LogManager.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUserAndSendEmail(@RequestBody SignUpDto signUpDto) {
        LOGGER.info("Enter registerUserAndSendEmail() in AuthenticationController." + signUpDto.getEmail());
        User user;
        UserDto userDto = new UserDto();
        SignUpDto.passwordValidation(signUpDto.getPassword());
        user = authenticationService.registerUserAndSendEmail(signUpDto);
        BeanUtils.copyProperties(user, userDto);
        return ResponseEntity.ok().body(userDto);
    }

    @PutMapping("/verification")
    public ResponseEntity<UserDto> emailVerificationFromRegisteredUser(@RequestParam("token") String confirmationToken) {
        LOGGER.info("Enter emailVerificationFromRegisteredUser() in AuthenticationController.");
        UserDto userDto = new UserDto();
        User user = authenticationService.emailVerificationFromRegisteredUser(confirmationToken);
        CurrentUser.setUser(user);
        BeanUtils.copyProperties(user, userDto);
        return ResponseEntity.ok().body(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody SignUpDto signUpDto) {
        LOGGER.info("Enter login() in AuthenticationController.");
        UserDto userDto = new UserDto();
        final User user = authenticationService.loadUserByLogin(signUpDto);
        CurrentUser.setUser(user);
        BeanUtils.copyProperties(user, userDto);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("Enter logout() in AuthenticationController.");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String status = null;
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            status = "SUCCESS LOGOUT";
        }
        return ResponseEntity.ok(status);
    }


    @GetMapping("/pwd-reset")
    public ResponseEntity<String> sendEmailForPasswordResetRequest(@RequestParam("email") String email) {
        LOGGER.info("Enter sendEmailForPasswordResetRequest() in UserController.");
        String response = this.authenticationService.sendEmailForPasswordResetRequest(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pwd-verification")
    public ResponseEntity<UserDto> validatePasswordResetRequestAndSaveNewPassword(@RequestParam("token") String token, @RequestBody PasswordResetDto passwordResetDto) {
        LOGGER.info("Enter validatePasswordResetRequestAndSaveNewPassword() in UserController.");
        UserDto userDto = new UserDto();
        User user = this.authenticationService.validatePasswordResetRequestAndSaveNewPassword(token, passwordResetDto);
        BeanUtils.copyProperties(user, userDto);
        return ResponseEntity.ok(userDto);
    }
}