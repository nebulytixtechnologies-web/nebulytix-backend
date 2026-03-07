package com.nebulytix.repository;

import com.nebulytix.entity.Lead;
import com.nebulytix.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    Page<Lead> findByAssignedTo(User assignedTo, Pageable pageable);
    Page<Lead> findByStatus(Lead.Status status, Pageable pageable);
    long countByStatus(Lead.Status status);
}