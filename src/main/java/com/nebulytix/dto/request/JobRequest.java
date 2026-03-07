package com.nebulytix.dto.request;

import com.nebulytix.entity.JobOpening;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JobRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String department;
    private String location;
    private String description;
    private String requirements;
    
    @NotNull(message = "Job type is required")
    private JobOpening.JobType type;
    
    private LocalDate expiryDate;
}