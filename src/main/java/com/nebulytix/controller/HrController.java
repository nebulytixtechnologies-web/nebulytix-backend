package com.nebulytix.controller;

import com.nebulytix.dto.request.ApplicationRequest;
import com.nebulytix.dto.request.JobRequest;
import com.nebulytix.dto.response.ApiResponse;
import com.nebulytix.entity.JobApplication;
import com.nebulytix.entity.JobOpening;
import com.nebulytix.entity.User;
import com.nebulytix.service.ApplicationService;
import com.nebulytix.service.JobService;
import com.nebulytix.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hr")
@RequiredArgsConstructor
public class HrController {
    
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final UserService userService;
    
    // Job Opening Management
    @PostMapping("/jobs")
    public ApiResponse<JobOpening> createJob(
            @Valid @RequestBody JobRequest request,
            @AuthenticationPrincipal UserDetails hrDetails) {
        User hr = userService.findByEmail(hrDetails.getUsername());
        return ApiResponse.success(jobService.createJob(request, hr));
    }
    
    @PutMapping("/jobs/{id}")
    public ApiResponse<JobOpening> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request) {
        return ApiResponse.success(jobService.updateJob(id, request));
    }
    
    @DeleteMapping("/jobs/{id}")
    public ApiResponse<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ApiResponse.success("Job deleted successfully", null);
    }
    
    @GetMapping("/jobs")
    public ApiResponse<Page<JobOpening>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails hrDetails) {
        User hr = userService.findByEmail(hrDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size, Sort.by("postedAt").descending());
        
        if (hr.getRole() == User.Role.ADMIN) {
            return ApiResponse.success(jobService.getAllJobs(pageable));
        } else {
            return ApiResponse.success(jobService.getJobsByHr(hr, pageable));
        }
    }
    
    // Job Applications Management
    @GetMapping("/jobs/{jobId}/applications")
    public ApiResponse<Page<JobApplication>> getJobApplications(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        return ApiResponse.success(applicationService.getApplicationsByJob(jobId, pageable));
    }
    
    @PutMapping("/applications/{id}/status")
    public ApiResponse<JobApplication> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam JobApplication.ApplicationStatus status) {
        return ApiResponse.success(applicationService.updateApplicationStatus(id, status));
    }
    
    @GetMapping("/applications")
    public ApiResponse<Page<JobApplication>> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        return ApiResponse.success(applicationService.getAllApplications(pageable));
    }
}