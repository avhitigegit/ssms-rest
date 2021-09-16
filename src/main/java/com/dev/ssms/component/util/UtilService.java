package com.dev.ssms.component.util;

import com.dev.ssms.component.enums.Status;
import com.dev.ssms.exception.BadRequestException;
import com.dev.ssms.model.User;
import com.dev.ssms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class UtilService {

    private final UserRepository userRepository;

    @Autowired
    public UtilService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Generate four digit code
    public static String generateFourDigitCode() {
        Random random = new Random();
        Integer fourDigitCode = random.nextInt(10000);
        return fourDigitCode.toString();
    }

    //Generate random String
    public static String generateRandomString() {
        String randomString = UUID.randomUUID().toString().replace("-", "");
        return randomString;
    }

    //Create the Password
    public static String bCryptPassword(String password) {
        if (password != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);
            //todo- add a salt to password
            return hashedPassword;
        } else {
            throw new BadRequestException("Does Not Pass The Correct Password.");
        }
    }

    //Check user availability
    public boolean checkUserAvailability(String email) {
        Boolean exist;
        User user = userRepository.findByEmailAndStatusSeq(email, Status.APPROVED.getStatusSeq());
        if (user != null){
            exist = true;
        } else {
            exist = false;
        }
        return exist;
    }

    //Checking email generated pin with actual pin
    public Boolean matchingEmailGeneratedPinWithActual(String email, String pinFromEmail) {
        Boolean status = null;
        User user = userRepository.findByEmailAndStatusSeq(email, Status.PENDING.getStatusSeq());
        String pinFromDatabase = user.getEmailVerifiedCode();
        if (pinFromDatabase.equalsIgnoreCase(pinFromEmail)){
            status = true;
        } else {
            status = false;
        }
//        if (status == true) {
//            return status;
//        } else {
//            throw new BadRequestException("Invalid Pin.");
//        }
        return status;
    }
}
