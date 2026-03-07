package com.nebulytix.controller;

import com.nebulytix.config.JwtService;
import com.nebulytix.dto.request.LoginRequest;
import com.nebulytix.dto.response.ApiResponse;
import com.nebulytix.dto.response.AuthResponse;
import com.nebulytix.entity.User;
import com.nebulytix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        
        return ApiResponse.success(AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .expiresIn(jwtService.getExpirationTime()) // You'll need to add this method
                .build());
    }
    
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ApiResponse.error("Invalid refresh token");
        }
        
        String refreshToken = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(refreshToken);
        
        if (userEmail != null) {
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                userEmail, "", java.util.Collections.emptyList());
            
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                User user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                
                String newAccessToken = jwtService.generateToken(userDetails);
                
                return ApiResponse.success(AuthResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .tokenType("Bearer")
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .role(user.getRole())
                        .expiresIn(jwtService.getExpirationTime())
                        .build());
            }
        }
        
        return ApiResponse.error("Invalid refresh token");
    }
}