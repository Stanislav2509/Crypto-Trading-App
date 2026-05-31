package com.cryptotrading.Crypto.Trading.App.service.impl;

import com.cryptotrading.Crypto.Trading.App.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationCode(String to, String code) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("s.ivov2509@gmail.com");
        message.setTo(to);
        message.setSubject("Verify your email");
        message.setText("Your verification code is: " + code);

        mailSender.send(message);
    }
}