package com.nebulytix.dto.request;

import lombok.Data;

@Data
public class ServiceRequest {

    private String title;
    private String description;
    private String icon;
    private Boolean status;
}