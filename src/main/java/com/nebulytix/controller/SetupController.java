package com.nebulytix.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nebulytix.entity.User;
import com.nebulytix.repository.UserRepository;

@RestController
@RequestMapping("/setup")
public class SetupController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/create-admin")
    public String createAdmin() {
        if (!userRepository.findByEmail("admin@nebulytix.com").isPresent()) {
            User admin = new User();
            admin.setEmail("admin@nebulytix.com");
            admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
            admin.setFullName("System Admin");
            admin.setRole(User.Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
            return "Admin user created successfully!";
        }
        return "Admin user already exists!";
    }
}
