package com.nebulytix.mapper;

import com.nebulytix.dto.request.ServiceRequest;
import com.nebulytix.dto.response.ServiceResponse;
import com.nebulytix.entity.ServiceEntity;
public class ServiceMapper {

    public static ServiceEntity toEntity(ServiceRequest request){
        return ServiceEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .icon(request.getIcon())
                .status(request.getStatus())
                .build();
    }

    public static ServiceResponse toResponse(ServiceEntity entity){
        return ServiceResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .icon(entity.getIcon())
                .status(entity.getStatus())
                .build();
    }
}