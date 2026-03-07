package com.nebulytix.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "leads")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Lead {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    private String phone;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "service_interest")
    private String serviceInterest;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Source source;
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    public enum Source {
        CONTACT_FORM, WHATSAPP_CLICK
    }
    
    public enum Status {
        NEW, CONTACTED, QUALIFIED
    }
}