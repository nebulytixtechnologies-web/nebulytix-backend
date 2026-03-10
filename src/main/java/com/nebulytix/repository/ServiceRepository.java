package com.nebulytix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nebulytix.entity.ServiceEntity;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
	@Query("SELECT s FROM ServiceEntity s WHERE s.deleted = false OR s.deleted IS NULL")
	List<ServiceEntity> findActiveServices();
}