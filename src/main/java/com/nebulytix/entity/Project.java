package com.nebulytix.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    private String category;
    
    @Column(length = 500)
    private String technologies;
    
    @Column(name = "live_url", length = 500)
    private String liveUrl;
    
    @Column(name = "project_date")
    private LocalDate projectDate;
}