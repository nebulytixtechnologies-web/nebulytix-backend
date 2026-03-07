package com.nebulytix.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_openings")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class JobOpening {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    private String department;
    private String location;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String requirements;
    
    @Enumerated(EnumType.STRING)
    private JobType type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by")
    private User postedBy;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @CreatedDate
    @Column(name = "posted_at", updatable = false)
    private LocalDateTime postedAt;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    public enum JobType {
        FULL_TIME, PART_TIME, INTERN, CONTRACT
    }
}