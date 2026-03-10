package com.nebulytix.controller;

import com.nebulytix.dto.request.JobRequest;
import com.nebulytix.dto.request.ProjectRequest;
import com.nebulytix.dto.request.ServiceRequest;
import com.nebulytix.dto.request.UserCreateRequest;
import com.nebulytix.dto.response.ApiResponse;
import com.nebulytix.entity.JobApplication;
import com.nebulytix.entity.JobOpening;
import com.nebulytix.dto.response.ServiceResponse;
import com.nebulytix.entity.Lead;
import com.nebulytix.entity.Project;
import com.nebulytix.entity.User;
import com.nebulytix.service.ApplicationService;
import com.nebulytix.service.JobService;
import com.nebulytix.service.LeadService;
import com.nebulytix.service.ProjectService;
import com.nebulytix.service.ServiceService;
import com.nebulytix.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserService userService;
    private final LeadService leadService;
    private final ProjectService projectService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final ServiceService service;
    
    // User Management
    @PostMapping("/users")
    public ApiResponse<User> createSubAdmin(
            @Valid @RequestBody UserCreateRequest request,
            @AuthenticationPrincipal UserDetails adminDetails) {
        User admin = userService.findByEmail(adminDetails.getUsername());
        return ApiResponse.success(userService.createSubAdmin(request, admin));
    }
    
    @GetMapping("/users")
    public ApiResponse<Page<User>> getAllSubAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success(userService.getAllSubAdmins(pageable));
    }
    
    @PutMapping("/users/{id}/deactivate")
    public ApiResponse<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ApiResponse.success("User deactivated successfully", null);
    }
    
    // Lead Management
    @GetMapping("/leads")
    public ApiResponse<Page<Lead>> getAllLeads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Lead.Status status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        if (status != null) {
            return ApiResponse.success(leadService.getLeadsByStatus(status, pageable));
        }
        return ApiResponse.success(leadService.getAllLeads(pageable));
    }
    
    @PutMapping("/leads/{id}/assign")
    public ApiResponse<Lead> assignLead(
            @PathVariable Long id,
            @RequestParam Long subAdminId) {
        return ApiResponse.success(leadService.assignLead(id, subAdminId));
    }
    
    @PutMapping("/leads/{id}/status")
    public ApiResponse<Lead> updateLeadStatus(
            @PathVariable Long id,
            @RequestParam Lead.Status status) {
        return ApiResponse.success(leadService.updateLeadStatus(id, status));
    }
    
    // Project Management
    @PostMapping("/projects")
    public ApiResponse<Project> createProject(@Valid @ModelAttribute ProjectRequest request) {
        return ApiResponse.success(projectService.createProject(request));
    }
    
    @PutMapping("/projects/{id}")
    public ApiResponse<Project> updateProject(
            @PathVariable Long id,
            @Valid @ModelAttribute ProjectRequest request) {
        return ApiResponse.success(projectService.updateProject(id, request));
    }
    
    @DeleteMapping("/projects/{id}")
    public ApiResponse<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ApiResponse.success("Project deleted successfully", null);
    }
    
    /**
     * GET ALL LEADS (ADMIN DASHBOARD)
     */
    @GetMapping
    public ApiResponse<Page<Lead>> getAllLeads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return ApiResponse.success(leadService.getAllLeads(pageable));
    }

    /**
     * GET SINGLE LEAD DETAILS
     */
    @GetMapping("/{id}")
    public ApiResponse<Lead> getLeadById(@PathVariable Long id) {

        return ApiResponse.success(leadService.getLeadById(id));
    }
    
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
    @PostMapping("add/service")
    public ApiResponse<ServiceResponse> create(@RequestBody ServiceRequest request) {
        return ApiResponse.success(service.create(request));
    }

   

    @PutMapping("update/service/{id}")
    public ApiResponse<ServiceResponse> update(
            @PathVariable Long id,
            @RequestBody ServiceRequest request) {
        return ApiResponse.success(service.update(id, request));
    }

    @DeleteMapping("deleteService/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
        ApiResponse.success("Service Deleted successfully",null);
    }
    
    @DeleteMapping("/softDeleteService/{id}")
    public ApiResponse<String> softDelete(@PathVariable Long id) {
        service.softDelete(id);
        return ApiResponse.success("Service soft deleted", null);
    }
}