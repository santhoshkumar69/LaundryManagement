package com.laundry.LaundryManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.laundry.LaundryManagement.model.OutletDetails;

@Repository
public interface OutletDetailsRepo extends JpaRepository<OutletDetails, Integer> {

	long countByTenantId(int tenantId);

	/** All outlets, including deleted ones. */
	List<OutletDetails> findByTenantIdOrderByIdAsc(int tenantId);

	@Query("from OutletDetails o where o.tenantId = :tenantId and o.activeStatus = 1 order by o.id")
	List<OutletDetails> findActiveByTenantId(@Param("tenantId") int tenantId);

	@Query("select o.id from OutletDetails o where o.activeStatus = 0")
	List<Integer> findDeletedIds();
}
