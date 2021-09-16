package com.dev.ssms.service.serviceImpl;

import com.dev.ssms.exception.EmailSendingException;
import com.dev.ssms.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Component
public class GmailEmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String mailTo, String mailSubject, String mailBody) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(mailTo);
            email.setSubject(mailSubject);
            email.setText(mailBody);
            javaMailSender.send(email);
        } catch (Exception e) {
            throw new EmailSendingException("Email Not Sending !");
        }
    }
}
