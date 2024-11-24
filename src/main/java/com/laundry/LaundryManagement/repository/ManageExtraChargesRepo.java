package com.laundry.LaundryManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.model.ExtraCharges;

@Repository
@Transactional(readOnly = false)
public interface ManageExtraChargesRepo extends JpaRepository<ExtraCharges, Long> {
	
	ExtraCharges findByChargeName(String chrgeName);
	
	@Query("From ExtraCharges extraCharges where extraCharges.activeStatus = 0")
	List<ExtraCharges> findInactiveExtraCharge();
	
	@Query("From ExtraCharges extraCharges where extraCharges.activeStatus = 1")
	List<ExtraCharges> findActiveExtraCharges();

}
