package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.WashingType;

public interface WashingTypeService {

	
	boolean createWashingType(WashingType washingType);
	
	List<WashingType> findAllWashingTYpe();
	
	List<WashingType> findDeActiveWashingType();
	
	WashingType findByRow(Integer processTypeId);
	
	Optional<WashingType> findIdFromDb(Integer processTypeId);
	
	WashingType findByProcessTypeName(String processTypeName);
	
	WashingType findByIdss(Integer processTypeId);
}
