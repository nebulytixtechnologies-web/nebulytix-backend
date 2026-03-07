package com.nebulytix.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class JobApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobOpening job;
    
    @Column(name = "applicant_name", nullable = false)
    private String applicantName;
    
    @Column(nullable = false)
    private String email;
    
    private String phone;
    
    @Column(name = "resume_url", length = 500, nullable = false)
    private String resumeUrl;
    
    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;
    
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.APPLIED;
    
    @CreatedDate
    @Column(name = "applied_at", updatable = false)
    private LocalDateTime appliedAt;
    
    public enum ApplicationStatus {
        APPLIED, REVIEWED, REJECTED, HIRED
    }
}