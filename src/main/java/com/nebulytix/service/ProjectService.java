package com.nebulytix.service;

import com.nebulytix.dto.request.ProjectRequest;
import com.nebulytix.exception.ResourceNotFoundException;
import com.nebulytix.entity.Project;
import com.nebulytix.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final String uploadDir = "uploads/projects/";
    
    @Transactional
    public Project createProject(ProjectRequest request) {
        Project project = new Project();
        updateProjectFromRequest(project, request);
        
        return projectRepository.save(project);
    }
    
    @Transactional
    public Project updateProject(Long id, ProjectRequest request) {
        Project project = findById(id);
        updateProjectFromRequest(project, request);
        return projectRepository.save(project);
    }
    
    private void updateProjectFromRequest(Project project, ProjectRequest request) {
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setCategory(request.getCategory());
        project.setTechnologies(request.getTechnologies());
        project.setLiveUrl(request.getLiveUrl());
        project.setProjectDate(request.getProjectDate());
        
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            // Delete old image if exists
            if (project.getImageUrl() != null) {
                deleteImage(project.getImageUrl());
            }
            String imageUrl = saveImage(request.getImage());
            project.setImageUrl(imageUrl);
        }
    }
    
    @Transactional
    public void deleteProject(Long id) {
        Project project = findById(id);
        if (project.getImageUrl() != null) {
            deleteImage(project.getImageUrl());
        }
        projectRepository.delete(project);
    }
    
    public Page<Project> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }
    
    public Project getProjectById(Long id) {
        return findById(id);
    }
    
    private Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }
    
    private String saveImage(MultipartFile file) {
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
            
            return "/uploads/projects/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage(), e);
        }
    }
    
    private void deleteImage(String imageUrl) {
        try {
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir + filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log error but don't throw exception
            System.err.println("Failed to delete image: " + e.getMessage());
        }
    }
}