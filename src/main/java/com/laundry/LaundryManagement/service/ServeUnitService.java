package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.Unit;

public interface ServeUnitService {
	
	    boolean createUnit(Unit unit);
	
		List<Unit> fetchUnitDetails();
		
		void deactivateUnit(Integer id);
	
		Unit findById(Integer id);
	
		Unit findByUnit(String unit);
	
		Optional<Unit> findOne(Integer id);
		
		List<Unit> fetchAllDeActiveUnit();

}
