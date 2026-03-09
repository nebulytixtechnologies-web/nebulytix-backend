package com.nebulytix.controller;

import com.nebulytix.dto.request.ProjectRequest;
import com.nebulytix.dto.request.UserCreateRequest;
import com.nebulytix.dto.response.ApiResponse;
import com.nebulytix.entity.Lead;
import com.nebulytix.entity.Project;
import com.nebulytix.entity.User;
import com.nebulytix.service.LeadService;
import com.nebulytix.service.ProjectService;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserService userService;
    private final LeadService leadService;
    private final ProjectService projectService;
    
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
}