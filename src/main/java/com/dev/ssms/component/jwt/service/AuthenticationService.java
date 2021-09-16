package com.dev.ssms.component.jwt.service;

import com.dev.ssms.component.constant.AppConstant;
import com.dev.ssms.component.enums.Role;
import com.dev.ssms.component.enums.Status;
import com.dev.ssms.component.jwt.model.SignUpDto;
import com.dev.ssms.component.util.UtilService;
import com.dev.ssms.dto.PasswordResetDto;
import com.dev.ssms.exception.BadRequestException;
import com.dev.ssms.exception.BadResponseException;
import com.dev.ssms.exception.UnauthorizedException;
import com.dev.ssms.model.Authentication;
import com.dev.ssms.model.User;
import com.dev.ssms.repository.AuthenticationRepository;
import com.dev.ssms.repository.UserRepository;
import com.dev.ssms.service.EmailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {
    private static final Logger LOGGER = LogManager.getLogger(AuthenticationService.class);

    private final UtilService utilService;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final EmailService emailService;
    private final AuthenticationRepository authenticationRepository;

    @Autowired
    public AuthenticationService(UtilService utilService,
                                 UserRepository userRepository,
                                 JwtTokenUtil jwtTokenUtil,
                                 EmailService emailService,
                                 AuthenticationRepository authenticationRepository) {
        this.utilService = utilService;
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.emailService = emailService;
        this.authenticationRepository = authenticationRepository;
    }

    public User registerUserAndSendEmail(SignUpDto signUpDto) {
        LOGGER.info("Enter registerUserAndSendEmail() in AuthenticationService.");
        User user = new User();
        try {
            if (signUpDto.getRole().equals(Role.CUSTOMER.getRole())) {
                user.setRoleSeq(Role.CUSTOMER.getRoleSeq());
            } else if (signUpDto.getRole().equals(Role.USER.getRole())) {
                user.setRoleSeq(Role.USER.getRoleSeq());
            }
            BeanUtils.copyProperties(signUpDto, user);
            if (this.utilService.checkUserAvailability(user.getEmail()) == false) {
                //No existing user. Create new user
                user = User.initFrom(user);
                this.userRepository.saveAndFlush(user);
                Authentication authentication = Authentication.initFrom(signUpDto.getPassword());
                this.authenticationRepository.saveAndFlush(authentication);
                String token = this.jwtTokenUtil.generateTokenWithPinAndEmail(user);
                String emailBody = AppConstant.EMAIL_VERIFICATION_BODY + token;
                this.emailService.sendEmail(user.getEmail(), AppConstant.EMAIL_VERIFICATION_SUBJECT, emailBody);
                user.setPasswordVerifiedCode(emailBody);
//                return user;
            } else if (this.userRepository.findByEmailAndStatus(user.getEmail(), Status.PENDING.getStatusSeq()).getEmail().equals(user.getEmail())) {
                user = this.userRepository.findByEmailAndStatus(user.getEmail(), Status.PENDING.getStatusSeq());
                this.userRepository.save(user);
                String token = this.jwtTokenUtil.generateTokenWithPinAndEmail(user);
                String emailBody = AppConstant.EMAIL_VERIFICATION_BODY + token;
                this.emailService.sendEmail(user.getEmail(), AppConstant.EMAIL_VERIFICATION_SUBJECT, emailBody);
                user.setPasswordVerifiedCode(emailBody);
//                return user;
            }
            return user;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public User emailVerificationFromRegisteredUser(String token) {
        LOGGER.info("Enter emailVerificationFromRegisteredUser() in UserService.");
        List<String> UserEmailAndPinFromToken = this.jwtTokenUtil.getUserEmailAndPinFromToken(token);
        String generatedPinOfToken = UserEmailAndPinFromToken.get(0);
        String userEmailOfToken = UserEmailAndPinFromToken.get(1);
        User user = null;
        try {
            user = this.userRepository.findByEmailAndStatus(userEmailOfToken, Status.PENDING.getStatusSeq());
            if (user != null) {
                if (this.utilService.matchingEmailGeneratedPinWithActual(userEmailOfToken, generatedPinOfToken) == true && user.getStatus().equals(Status.PENDING.getStatusSeq())) {
                    user.setIsEmailVerified(true);
                    user.setStatus(Status.APPROVED.getStatusSeq());
                    user.setRoleSeq(Role.USER.getRoleSeq());
                    this.userRepository.saveAndFlush(user);
                    user = this.userRepository.findByEmailAndStatus(userEmailOfToken, Status.APPROVED.getStatusSeq());
                } else {
                    throw new BadResponseException("The Request Cannot Be Fulfilled Due To Bad Syntax Exception.");
                }
            } else {
                throw new BadResponseException("Resource Not Found Exception.");
            }
        } catch (Exception e) {
            throw new BadResponseException(e.getMessage());
        }
        return user;
    }

    public User loadUserByLogin(SignUpDto signUpDto) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = this.userRepository.findByEmailAndStatus(signUpDto.getEmail(), Status.APPROVED.getStatusSeq());
        if (user.getStatus().equals(Status.APPROVED.getStatusSeq())) {
            Authentication authentication = authenticationRepository.findByUser(user.getUserSeq());
            String hashedPassword = authentication.getPassword();
            if (passwordEncoder.matches(signUpDto.getPassword(), hashedPassword)) {
                return user;
            } else {
                throw new UnauthorizedException("Authentication Failed. Your Password incorrect");
            }
        } else {
            throw new UnauthorizedException("Authentication Failed. Your Username incorrect");
        }
    }

    public String sendEmailForPasswordResetRequest(String email) {
        LOGGER.info("Enter sendEmailForPasswordResetRequest() in UserService.");
        String response = null;
        try {
            User user = this.userRepository.findByEmailAndStatus(email, Status.APPROVED.getStatusSeq());
            if (user != null) {
                user.setPasswordVerifiedCode(UtilService.generateRandomString());
                user.setIsPasswordReset(false);
                this.userRepository.saveAndFlush(user);
                String token = this.jwtTokenUtil.generateTokenWithPinAndEmail(user);
                String emailBody = AppConstant.PASSWORD_RESET_VERIFICATION_BODY + token;
                this.emailService.sendEmail(user.getEmail(), AppConstant.PASSWORD_RESET_VERIFICATION_SUBJECT, emailBody);
                response = "Email Send Successfully " + emailBody;
            } else {
                throw new BadResponseException("Resource Not Found Exception.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public User validatePasswordResetRequestAndSaveNewPassword(String token, PasswordResetDto passwordResetDto) {
        LOGGER.info("Enter validatePasswordResetRequestAndSaveNewPassword() in UserService.");
        User user = new User();
        try {
            List<String> UserEmailAndPinFromToken = this.jwtTokenUtil.getUserEmailAndPinFromToken(token);
            String generatedPinOfToken = UserEmailAndPinFromToken.get(0);
            String userEmailOfToken = UserEmailAndPinFromToken.get(1);
            user = this.userRepository.findByEmailAndStatus(userEmailOfToken, Status.APPROVED.getStatusSeq());
            if (user != null && passwordResetDto.getNewPassword().equals(passwordResetDto.getConfirmPassword()) && user.getIsPasswordReset() == false) {
                if (utilService.matchingEmailGeneratedPinWithActual(userEmailOfToken, generatedPinOfToken) == true) {
                    user.setIsPasswordReset(true);
                    this.userRepository.saveAndFlush(user);
                    user = this.userRepository.findByUserSeqAndStatus(user.getUserSeq(), Status.APPROVED.getStatusSeq());
                    Authentication authentication = this.authenticationRepository.findByUser(user.getUserSeq());
                    authentication.setPassword(UtilService.bCryptPassword(passwordResetDto.getNewPassword()));
                    this.authenticationRepository.saveAndFlush(authentication);
                }
            } else {
                throw new BadResponseException("Resource Not Found Exception.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

}
