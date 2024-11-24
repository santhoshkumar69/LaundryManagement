package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.laundry.LaundryManagement.model.WashingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.repository.WashingTypeRepo;

@Service
public class WashingTypeServiceImpl implements WashingTypeService {

	@Autowired
	WashingTypeRepo washingTypeRepo;
	
	@Override
	@Transactional
	public boolean createWashingType(WashingType washingType) {
		try
		{
			washingTypeRepo.save(washingType);
			return true;
		}catch (Exception Ex)
		{
			Ex.printStackTrace();
			return false;
		}
		
	}

	@Override
	@Transactional
	public List<WashingType> findAllWashingTYpe() {
		
		return washingTypeRepo.findAllWashingType();
	}

	@Override
	public List<WashingType> findDeActiveWashingType() {
		
		return washingTypeRepo.findDeActiveWashingType();
	}

	@Override
	public WashingType findByRow(Integer Id) {
		
		return washingTypeRepo.findByIds(Id);
	}

	@Override
	public Optional<WashingType> findIdFromDb(Integer id) {
		
		return washingTypeRepo.findByIdRow(id);
	}

	@Override
	public WashingType findByIdss(Integer id) {
		
		return washingTypeRepo.findByIdss(id);
	}

	@Override
	public WashingType findByProcessTypeName(String processTypeName) {
		return washingTypeRepo.findProcessTypeName(processTypeName);
	}
	
	
	

}
