package com.nebulytix.dto.response;

import com.nebulytix.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long id;
    private String email;
    private String fullName;
    private User.Role role;
    private long expiresIn;
}