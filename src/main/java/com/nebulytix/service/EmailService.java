package com.nebulytix.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendWelcomeEmail(String toEmail, String name, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to Nebulytix - Your Account Details");
        message.setText(String.format(
            "Dear %s,\n\n" +
            "Your sub-admin account has been created successfully.\n\n" +
            "Login credentials:\n" +
            "Email: %s\n" +
            "Temporary Password: %s\n\n" +
            "Please login and change your password immediately.\n\n" +
            "Best regards,\n" +
            "Nebulytix Team",
            name, toEmail, tempPassword
        ));
        
        mailSender.send(message);
    }
    
    public void sendLeadNotification(String toEmail, String leadName, String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(fromEmail);
        email.setTo(toEmail);
        email.setSubject("New Lead Received - Nebulytix");
        email.setText(String.format(
            "New lead received from %s.\n\nMessage: %s",
            leadName, message
        ));
        
        mailSender.send(email);
    }
}