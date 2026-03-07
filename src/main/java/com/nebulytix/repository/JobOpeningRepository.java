package com.nebulytix.repository;

import com.nebulytix.entity.JobOpening;
import com.nebulytix.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface JobOpeningRepository extends JpaRepository<JobOpening, Long> {
    Page<JobOpening> findByIsActiveTrue(Pageable pageable);
    Page<JobOpening> findByPostedBy(User postedBy, Pageable pageable);
    
    @Query("SELECT j FROM JobOpening j WHERE j.isActive = true AND (j.expiryDate IS NULL OR j.expiryDate > :currentDate)")
    Page<JobOpening> findAllActiveJobs(LocalDate currentDate, Pageable pageable);
}