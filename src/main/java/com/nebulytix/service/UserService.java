package com.nebulytix.service;

import com.nebulytix.dto.request.UserCreateRequest;
import com.nebulytix.exception.ResourceNotFoundException;
import com.nebulytix.entity.User;
import com.nebulytix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    @Transactional
    public User createSubAdmin(UserCreateRequest request, User admin) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        User subAdmin = new User();
        subAdmin.setEmail(request.getEmail());
        subAdmin.setFullName(request.getFullName());
        subAdmin.setRole(User.Role.SUB_ADMIN);
        subAdmin.setCreatedBy(admin);
        subAdmin.setActive(true);
        
        // Generate temporary password
        String tempPassword = generateTemporaryPassword();
        subAdmin.setPasswordHash(passwordEncoder.encode(tempPassword));
        
        User savedUser = userRepository.save(subAdmin);
        
        // Send email with temporary password
        try {
            emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFullName(), tempPassword);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            e.printStackTrace();
        }
        
        return savedUser;
    }
    
    @Transactional
    public void deactivateUser(Long userId) {
        User user = findById(userId);
        user.setActive(false);
        userRepository.save(user);
    }
    
    public Page<User> getAllSubAdmins(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
    
    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8) + "A1@";
    }
}