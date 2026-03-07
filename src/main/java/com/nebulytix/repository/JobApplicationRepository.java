package com.nebulytix.repository;

import com.nebulytix.entity.JobApplication;
import com.nebulytix.entity.JobOpening;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    Page<JobApplication> findByJob(JobOpening job, Pageable pageable);
    boolean existsByEmailAndJob(String email, JobOpening job);
}