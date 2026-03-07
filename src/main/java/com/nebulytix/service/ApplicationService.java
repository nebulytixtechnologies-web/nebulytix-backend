package com.nebulytix.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nebulytix.dto.request.ApplicationRequest;
import com.nebulytix.entity.JobApplication;
import com.nebulytix.entity.JobOpening;
import com.nebulytix.exception.ResourceNotFoundException;
import com.nebulytix.repository.JobApplicationRepository;
import com.nebulytix.repository.JobOpeningRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    
    private final JobApplicationRepository applicationRepository;
    private final JobOpeningRepository jobRepository;
    private final String uploadDir = "uploads/resumes/";
    
    @Transactional
    public JobApplication submitApplication(ApplicationRequest request) {
        JobOpening job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        
        // Check if already applied
        if (applicationRepository.existsByEmailAndJob(request.getEmail(), job)) {
            throw new RuntimeException("You have already applied for this position");
        }
        
        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setApplicantName(request.getApplicantName());
        application.setEmail(request.getEmail());
        application.setPhone(request.getPhone());
        application.setCoverLetter(request.getCoverLetter());
        
        // Save resume
        String resumeUrl = saveResume(request.getResume());
        application.setResumeUrl(resumeUrl);
        
        return applicationRepository.save(application);
    }
    
    public Page<JobApplication> getApplicationsByJob(Long jobId, Pageable pageable) {
        JobOpening job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return applicationRepository.findByJob(job, pageable);
    }
    
    public Page<JobApplication> getAllApplications(Pageable pageable) {
        return applicationRepository.findAll(pageable);
    }
    
    @Transactional
    public JobApplication updateApplicationStatus(Long id, JobApplication.ApplicationStatus status) {
        JobApplication application = findById(id);
        application.setStatus(status);
        return applicationRepository.save(application);
    }
    
    private JobApplication findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    }
    
    private String saveResume(MultipartFile file) {
        try {
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            
            // Save file
            Files.copy(file.getInputStream(), filePath);
            
            return "/uploads/resumes/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save resume", e);
        }
    }
}