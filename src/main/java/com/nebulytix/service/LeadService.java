package com.nebulytix.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nebulytix.dto.request.LeadRequest;
import com.nebulytix.entity.Lead;
import com.nebulytix.entity.User;
import com.nebulytix.exception.ResourceNotFoundException;
import com.nebulytix.repository.LeadRepository;
import com.nebulytix.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeadService {
    
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public Lead createLead(LeadRequest request) {
        Lead lead = new Lead();
        lead.setName(request.getName());
        lead.setEmail(request.getEmail());
        lead.setPhone(request.getPhone());
        lead.setMessage(request.getMessage());
        lead.setServiceInterest(request.getServiceInterest());
        
        // Handle source conversion safely
        try {
            lead.setSource(Lead.Source.valueOf(request.getSource()));
        } catch (IllegalArgumentException e) {
            lead.setSource(Lead.Source.CONTACT_FORM); // Default value
        }
        
        lead.setStatus(Lead.Status.NEW);
        
        return leadRepository.save(lead);
    }
    
//    public Page<Lead> getAllLeads(Pageable pageable) {
//        return leadRepository.findAll(pageable);
//    }
    
    
    public Page<Lead> getLeadsByStatus(Lead.Status status, Pageable pageable) {
        return leadRepository.findByStatus(status, pageable);
    }
    
    public Page<Lead> getLeadsByAssignedTo(User assignedTo, Pageable pageable) {
        return leadRepository.findByAssignedTo(assignedTo, pageable);
    }
    
    @Transactional
    public Lead assignLead(Long leadId, Long subAdminId) {
        Lead lead = findById(leadId);
        User subAdmin = userRepository.findById(subAdminId)
                .orElseThrow(() -> new ResourceNotFoundException("Sub-admin not found with id: " + subAdminId));
        
        if (subAdmin.getRole() != User.Role.SUB_ADMIN) {
            throw new RuntimeException("User is not a sub-admin");
        }
        
        lead.setAssignedTo(subAdmin);
        return leadRepository.save(lead);
    }
    
    @Transactional
    public Lead updateLeadStatus(Long leadId, Lead.Status status) {
        Lead lead = findById(leadId);
        lead.setStatus(status);
        return leadRepository.save(lead);
    }
    
    public Lead findById(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
    }
    
    public long getLeadCountByStatus(Lead.Status status) {
        return leadRepository.countByStatus(status);
    }
   
    public Page<Lead> getAllLeads(Pageable pageable) {
        return leadRepository.findAll(pageable);
    }

  
    public Lead getLeadById(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + id));
    }
   
}