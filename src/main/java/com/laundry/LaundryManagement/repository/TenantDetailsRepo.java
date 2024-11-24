package com.laundry.LaundryManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.laundry.LaundryManagement.model.TenantDetails;

@Repository
public interface TenantDetailsRepo extends JpaRepository<TenantDetails, Integer> {

	@Query("From TenantDetails tenantDetails where tenantDetails.tenantCode = :tenantCode")
	TenantDetails findTenantDetails(@Param("tenantCode") String tenantCode);
	
}
