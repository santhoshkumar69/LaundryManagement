package com.laundry.LaundryManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.laundry.LaundryManagement.model.OutletDetails;

@Repository
public interface OutletDetailsRepo extends JpaRepository<OutletDetails, Integer> {

	long countByTenantId(int tenantId);

	List<OutletDetails> findByTenantIdOrderByIdAsc(int tenantId);
}
