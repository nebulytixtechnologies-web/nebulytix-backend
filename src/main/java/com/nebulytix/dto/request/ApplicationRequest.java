package com.nebulytix.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ApplicationRequest {
    
    @NotNull(message = "Job ID is required")
    private Long jobId;
    
    @NotBlank(message = "Applicant name is required")
    private String applicantName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    private String phone;
    private String coverLetter;
    
    @NotNull(message = "Resume is required")
    private MultipartFile resume;
}