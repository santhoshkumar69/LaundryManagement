package com.laundry.LaundryManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.laundry.LaundryManagement.model.InvoiceTemplate;

@Repository
public interface InvoiceTemplateRepository extends JpaRepository<InvoiceTemplate, Integer> {

	@Query("From InvoiceTemplate t where t.tenantId = :tenantId and t.activeStatus = 1 order by t.templateName")
	List<InvoiceTemplate> findLive(@Param("tenantId") Integer tenantId);

	@Query("From InvoiceTemplate t where t.tenantId = :tenantId and t.activeStatus = 1 and t.isActive = 1")
	List<InvoiceTemplate> findApplied(@Param("tenantId") Integer tenantId);

	@Query("From InvoiceTemplate t where t.tenantId = :tenantId and t.activeStatus = 1"
			+ " and lower(t.templateName) = lower(:name)")
	List<InvoiceTemplate> findLiveByName(@Param("tenantId") Integer tenantId, @Param("name") String name);
}
