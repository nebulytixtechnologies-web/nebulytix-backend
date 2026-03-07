package com.nebulytix.service;

import com.nebulytix.dto.request.JobRequest;
import com.nebulytix.exception.ResourceNotFoundException;
import com.nebulytix.entity.JobOpening;
import com.nebulytix.entity.User;
import com.nebulytix.repository.JobOpeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class JobService {
    
    private final JobOpeningRepository jobRepository;
    
    @Transactional
    public JobOpening createJob(JobRequest request, User postedBy) {
        JobOpening job = new JobOpening();
        updateJobFromRequest(job, request);
        job.setPostedBy(postedBy);
        job.setActive(true);
        
        return jobRepository.save(job);
    }
    
    @Transactional
    public JobOpening updateJob(Long id, JobRequest request) {
        JobOpening job = findById(id);
        updateJobFromRequest(job, request);
        return jobRepository.save(job);
    }
    
    @Transactional
    public void deleteJob(Long id) {
        JobOpening job = findById(id);
        job.setActive(false);
        jobRepository.save(job);
    }
    
    public Page<JobOpening> getAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }
    
    public Page<JobOpening> getAllActiveJobs(Pageable pageable) {
        return jobRepository.findAllActiveJobs(LocalDate.now(), pageable);
    }
    
    public Page<JobOpening> getJobsByHr(User hr, Pageable pageable) {
        return jobRepository.findByPostedBy(hr, pageable);
    }
    
    public JobOpening getJobById(Long id) {
        return findById(id);
    }
    
    private JobOpening findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    }
    
    private void updateJobFromRequest(JobOpening job, JobRequest request) {
        job.setTitle(request.getTitle());
        job.setDepartment(request.getDepartment());
        job.setLocation(request.getLocation());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setType(request.getType());
        job.setExpiryDate(request.getExpiryDate());
    }
}