package com.nebulytix.service;



import java.util.List;

import com.nebulytix.dto.request.ServiceRequest;
import com.nebulytix.dto.response.ServiceResponse;

public interface ServiceService {

    ServiceResponse create(ServiceRequest request);

    List<ServiceResponse> getAll();

    ServiceResponse getById(Long id);

    ServiceResponse update(Long id, ServiceRequest request);

    void delete(Long id);
    
    public void softDelete(Long id);
}