package com.nebulytix.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class ProjectRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    private String category;
    private String technologies;
    private String liveUrl;
    private LocalDate projectDate;
    private MultipartFile image;
}