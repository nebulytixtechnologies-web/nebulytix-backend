package com.nebulytix.service.impl;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nebulytix.dto.request.ServiceRequest;
import com.nebulytix.dto.response.ServiceResponse;
import com.nebulytix.entity.ServiceEntity;
import com.nebulytix.exception.ResourceNotFoundException;
import com.nebulytix.mapper.ServiceMapper;
import com.nebulytix.repository.ServiceRepository;
import com.nebulytix.service.ServiceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository repository;

    @Override
    public ServiceResponse create(ServiceRequest request) {

        ServiceEntity entity = ServiceMapper.toEntity(request);

        ServiceEntity saved = repository.save(entity);

        return ServiceMapper.toResponse(saved);
    }

    @Override
    public List<ServiceResponse> getAll() {

    	 return repository.findActiveServices()
    	            .stream()
    	            .map(ServiceMapper::toResponse)
    	            .collect(Collectors.toList());
    }

    @Override
    public ServiceResponse getById(Long id) {

        ServiceEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        return ServiceMapper.toResponse(entity);
    }

    @Override
    public ServiceResponse update(Long id, ServiceRequest request) {

        ServiceEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setIcon(request.getIcon());
        entity.setStatus(request.getStatus());

        ServiceEntity updated = repository.save(entity);

        return ServiceMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {

        ServiceEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        repository.delete(entity);
    }

	@Override
	public void softDelete(Long id) {
		
	        ServiceEntity entity = repository.findById(id)
	                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

	        entity.setDeleted(true);

	        repository.save(entity);
	  }
		
    
    
}