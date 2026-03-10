package com.nebulytix.dto.response;

import com.nebulytix.entity.ServiceEntity.ServiceEntityBuilder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceResponse {

    private Long id;
    private String title;
    private String description;
    private String icon;
    private Boolean status;

}