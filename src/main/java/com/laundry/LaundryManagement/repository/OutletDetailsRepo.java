package com.laundry.LaundryManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.laundry.LaundryManagement.model.OutletDetails;

@Repository
public interface OutletDetailsRepo extends JpaRepository<OutletDetails, Integer> {
	
	

}
