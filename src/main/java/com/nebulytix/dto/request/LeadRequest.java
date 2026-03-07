package com.nebulytix.dto.request;

import com.nebulytix.entity.Lead;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LeadRequest {
    
    private String name;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String phone;
    private String message;
    private String serviceInterest;
    
    @NotBlank(message = "Source is required")
    private String source; // CONTACT_FORM or WHATSAPP_CLICK
}