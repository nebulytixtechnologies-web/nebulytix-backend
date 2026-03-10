package com.nebulytix.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nebulytix.dto.request.ApplicationRequest;
import com.nebulytix.dto.request.LeadRequest;
import com.nebulytix.dto.response.ApiResponse;
import com.nebulytix.dto.response.ServiceResponse;
import com.nebulytix.entity.JobApplication;
import com.nebulytix.entity.JobOpening;
import com.nebulytix.entity.Lead;
import com.nebulytix.entity.Project;
import com.nebulytix.service.ApplicationService;
import com.nebulytix.service.JobService;
import com.nebulytix.service.LeadService;
import com.nebulytix.service.ProjectService;
import com.nebulytix.service.ServiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Public Controller - Handles all unauthenticated/public API endpoints
 * These endpoints are accessible without any authentication token
 * Base URL: /api/public
 */
@RestController
@RequestMapping("/public")  // All endpoints in this controller will start with /public
@RequiredArgsConstructor  // Lombok: Generates constructor for all final fields (dependency injection)
public class PublicController {
    
    // Dependency injection through constructor (thanks to @RequiredArgsConstructor)
    private final ProjectService projectService;  // Service for project-related operations
    private final LeadService leadService;        // Service for lead-related operations
    private final JobService jobService;          // Service for job-related operations
    private final ServiceService service;
    private final ApplicationService applicationService;
    /**
     * GET /api/public/projects
     * Retrieves all projects with pagination support
     * 
     * @param page - Page number (0-based index, default: 0)
     * @param size - Number of items per page (default: 10)
     * @return Paginated list of projects sorted by date (newest first)
     * 
     * Example: GET /api/public/projects?page=0&size=5
     */
    @GetMapping("/projects")
    public ApiResponse<Page<Project>> getAllProjects(
            @RequestParam(defaultValue = "0") int page,      // Page number, defaults to 0 if not provided
            @RequestParam(defaultValue = "10") int size) {   // Page size, defaults to 10 if not provided
        
        /**
         * PageRequest.of(page, size, sort) - Creates a pagination request
         * Sort.by("projectDate").descending() - Sorts results by projectDate field in descending order
         * 
         * Why descending? To show newest projects first (most recent at the top)
         * Equivalent SQL: SELECT * FROM projects ORDER BY project_date DESC LIMIT size OFFSET page*size
         */
        Pageable pageable = PageRequest.of(page, size, Sort.by("projectDate").descending());
        
        /**
         * Delegate to service layer and wrap response in ApiResponse
         * ApiResponse.success() creates a standardized success response
         */
        return ApiResponse.success(projectService.getAllProjects(pageable));
    }
    
    /**
     * GET /api/public/projects/{id}
     * Retrieves a single project by its ID
     * 
     * @param id - Project ID from URL path
     * @return Single project details
     * 
     * Example: GET /api/public/projects/1
     */
    @GetMapping("/projects/{id}")
    public ApiResponse<Project> getProjectById(@PathVariable Long id) {  // @PathVariable extracts ID from URL
        return ApiResponse.success(projectService.getProjectById(id));
    }
    
    /**
     * GET /api/public/jobs
     * Retrieves all ACTIVE job openings with pagination
     * Only shows jobs that are currently open for applications
     * 
     * @param page - Page number (0-based index, default: 0)
     * @param size - Number of items per page (default: 10)
     * @return Paginated list of active jobs sorted by posting date (newest first)
     * 
     * Example: GET /api/public/jobs?page=0&size=5
     */
    @GetMapping("/jobs")
    public ApiResponse<Page<JobOpening>> getAllActiveJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        /**
         * Sort.by("postedAt").descending() - Shows newest job postings first
         * This helps candidates see the most recent opportunities
         */
        Pageable pageable = PageRequest.of(page, size, Sort.by("postedAt").descending());
        
        /**
         * Note: getAllActiveJobs() internally filters to only return jobs where:
         * - isActive = true
         * - expiryDate is null OR expiryDate > current date
         */
        return ApiResponse.success(jobService.getAllActiveJobs(pageable));
    }
    
    /**
     * GET /api/public/jobs/{id}
     * Retrieves detailed information about a specific job opening
     * 
     * @param id - Job ID from URL path
     * @return Detailed job information
     * 
     * Example: GET /api/public/jobs/1
     */
    @GetMapping("/jobs/{id}")
    public ApiResponse<JobOpening> getJobById(@PathVariable Long id) {
        return ApiResponse.success(jobService.getJobById(id));
    }
    
    /**
     * POST /api/public/leads/contact
     * Handles contact form submissions from the website
     * This endpoint is public (no authentication required)
     * 
     * @param request - LeadRequest containing form data (validated)
     * @return Created lead with success message
     * 
     * Request Body Example:
     * {
     *   "name": "John Doe",
     *   "email": "john@example.com",
     *   "phone": "+1234567890",
     *   "message": "Interested in your services",
     *   "serviceInterest": "Web Development"
     * }
     */
    @PostMapping("/leads/contact")
    public ApiResponse<Lead> submitContactForm(@Valid @RequestBody LeadRequest request) {
        /**
         * Set the source to CONTACT_FORM to track where this lead came from
         * This helps in analytics and understanding which channels are most effective
         */
        request.setSource("CONTACT_FORM");
        
        /**
         * ApiResponse.success("message", data) - Returns success response with custom message
         * First parameter: Success message shown to user
         * Second parameter: The created lead data
         */
        return ApiResponse.success("Thank you for contacting us", leadService.createLead(request));
    }
    
    /**
     * POST /api/public/leads/whatsapp-click
     * Logs when a user clicks on the WhatsApp chat button
     * This is for analytics - tracks user interest without forcing them to fill a form
     * 
     * @param request - LeadRequest (can be empty or contain partial data)
     * @return Logged lead with success message
     * 
     * Request Body Example (optional fields):
     * {
     *   "phone": "+1234567890",           // Optional: if user provided phone
     *   "name": "John Doe",                // Optional: if user provided name
     *   "message": "Clicked from homepage" // Optional: context about where they clicked
     * }
     * 
     * Note: Even with empty body, this will create a lead record with source=WHATSAPP_CLICK
     */
    @PostMapping("/leads/whatsapp-click")
    public ApiResponse<Lead> logWhatsAppClick(@Valid @RequestBody LeadRequest request) {
        /**
         * Set source to WHATSAPP_CLICK to distinguish from contact form submissions
         * This helps track:
         * - How many users are interested in WhatsApp communication
         * - Which pages have the most WhatsApp engagement
         * - Conversion rates from WhatsApp clicks to actual chats
         */
        request.setSource("WHATSAPP_CLICK");
        
        return ApiResponse.success("WhatsApp click logged", leadService.createLead(request));
    }
    
    @GetMapping("getAll/service")
    public ApiResponse<List<ServiceResponse>> getAll() {
        return ApiResponse.success(service.getAll());
    }

    @GetMapping("getServiceById/{id}")
    public ApiResponse<ServiceResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(service.getById(id));
    }
    
    @PostMapping("/applyJob")
    public JobApplication apply(@ModelAttribute ApplicationRequest request) {
        return applicationService.submitApplication(request);
    }
}

/**
 * ====================================
 * SORTING EXPLANATION FOR FUTURE REFERENCE
 * ====================================
 * 
 * 1. BASIC SORTING:
 *    Sort.by("fieldName")                          // Ascending (A-Z, 0-9)
 *    Sort.by("fieldName").ascending()              // Same as above
 *    Sort.by("fieldName").descending()             // Descending (Z-A, 9-0)
 * 
 * 2. MULTIPLE FIELDS SORTING:
 *    Sort.by("field1").descending()
 *        .and(Sort.by("field2").ascending())       // Sort by field1 desc, then field2 asc
 * 
 *    // Alternative syntax:
 *    Sort sort = Sort.by(
 *        Sort.Order.desc("field1"),
 *        Sort.Order.asc("field2")
 *    );
 * 
 * 3. SORT WITH PAGINATION:
 *    PageRequest.of(page, size, sort)              // Apply sorting to paginated results
 * 
 * 4. COMMON SORTING SCENARIOS:
 *    
 *    // Newest first (most common for lists)
 *    Sort.by("createdAt").descending()
 *    Sort.by("postedAt").descending()
 *    Sort.by("projectDate").descending()
 *    
 *    // Alphabetical order
 *    Sort.by("title").ascending()
 *    Sort.by("name").ascending()
 *    
 *    // Priority order (high to low)
 *    Sort.by("priority").descending()
 *    
 *    // Price order (low to high for e-commerce)
 *    Sort.by("price").ascending()
 *    
 *    // Combined sorting (category first, then name)
 *    Sort.by("category").ascending()
 *        .and(Sort.by("name").ascending())
 * 
 * 5. ENTITY FIELD MAPPING:
 *    The field names in Sort.by() must match the entity field names, not database column names
 *    Example: If entity has "createdAt" field (database column "created_at"), use "createdAt"
 * 
 * 6. CASE SENSITIVITY:
 *    By default, sorting is case-sensitive. For case-insensitive sorting:
 *    - Use @Query with ORDER BY LOWER(field) in repository
 *    - Or implement custom sorting in service layer
 * 
 * 7. NULL HANDLING:
 *    By default, null values appear last in ascending order, first in descending
 *    To customize: @OrderBy("field ASC NULLS FIRST") in entity or custom query
 * 
 * 8. PERFORMANCE TIPS:
 *    - Always add database indexes on sorted fields
 *    - Use pagination with sorting to avoid memory issues
 *    - Consider composite indexes for multi-field sorting
 * 
 * 9. EXAMPLES WITH DIFFERENT SORT REQUIREMENTS:
 *    
 *    // Example 1: Get 10 most recent projects
 *    PageRequest.of(0, 10, Sort.by("projectDate").descending())
 *    
 *    // Example 2: Get jobs sorted by department, then by posting date
 *    Sort sort = Sort.by(
 *        Sort.Order.asc("department"),
 *        Sort.Order.desc("postedAt")
 *    );
 *    PageRequest.of(page, size, sort)
 *    
 *    // Example 3: Get leads sorted by status (priority order), then by date
 *    // Custom sorting might need @Query in repository
 *    
 *    // Example 4: Random sorting (use native query)
 *    // @Query(value = "SELECT * FROM projects ORDER BY RAND()", nativeQuery = true)
 */


//package com.nebulytix.controller;
//
//import com.nebulytix.dto.request.LeadRequest;
//import com.nebulytix.dto.response.ApiResponse;
//import com.nebulytix.entity.JobOpening;
//import com.nebulytix.entity.Lead;
//import com.nebulytix.entity.Project;
//import com.nebulytix.service.JobService;
//import com.nebulytix.service.LeadService;
//import com.nebulytix.service.ProjectService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/public")
//@RequiredArgsConstructor
//public class PublicController {
//    
//    private final ProjectService projectService;
//    private final LeadService leadService;
//    private final JobService jobService;
//    
//    @GetMapping("/projects")
//    public ApiResponse<Page<Project>> getAllProjects(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("projectDate").descending());
//        return ApiResponse.success(projectService.getAllProjects(pageable));
//    }
//    
//    @GetMapping("/projects/{id}")
//    public ApiResponse<Project> getProjectById(@PathVariable Long id) {
//        return ApiResponse.success(projectService.getProjectById(id));
//    }
//    
//    @GetMapping("/jobs")
//    public ApiResponse<Page<JobOpening>> getAllActiveJobs(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("postedAt").descending());
//        return ApiResponse.success(jobService.getAllActiveJobs(pageable));
//    }
//    
//    @GetMapping("/jobs/{id}")
//    public ApiResponse<JobOpening> getJobById(@PathVariable Long id) {
//        return ApiResponse.success(jobService.getJobById(id));
//    }
//    
//    @PostMapping("/leads/contact")
//    public ApiResponse<Lead> submitContactForm(@Valid @RequestBody LeadRequest request) {
//        request.setSource("CONTACT_FORM");
//        return ApiResponse.success("Thank you for contacting us", leadService.createLead(request));
//    }
//    
//    @PostMapping("/leads/whatsapp-click")
//    public ApiResponse<Lead> logWhatsAppClick(@Valid @RequestBody LeadRequest request) {
//        request.setSource("WHATSAPP_CLICK");
//        return ApiResponse.success("WhatsApp click logged", leadService.createLead(request));
//    }
//}