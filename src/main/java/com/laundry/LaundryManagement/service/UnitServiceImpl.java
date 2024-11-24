package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.repository.UnitRepo;

@Service
public class UnitServiceImpl implements ServeUnitService{

	@Autowired
	UnitRepo unitRepo;
	
	@Override
	@Transactional
	public boolean createUnit(Unit unit) {


		try {
			
			unitRepo.save(unit);
			return true;
		}catch (Exception Ex){
       Ex.printStackTrace();
       return false;
		}
	}

	@Override
	@Transactional
	public List<Unit> fetchUnitDetails() {
		return unitRepo.findAll();
	}

	@Override
	@Transactional
	public void deactivateUnit(Integer id) {
		 unitRepo.deactivateUnit(id);
		
	}

	@Override
	@Transactional
	public Unit findById(Integer id) {
		
		return unitRepo.findByUnit(id);
	}

	@Override
	public Unit findByUnit(String unit) {
		// TODO Auto-generated method stub
		return unitRepo.findUnit(unit);
	}

	@Override
	public Optional<Unit> findOne(Integer id) {
		// TODO Auto-generated method stub
		return unitRepo.findByIds(id);
	}

	@Override
	public List<Unit> fetchAllDeActiveUnit() {
		// TODO Auto-generated method stub
		return unitRepo.getAllDeActiveUnit();
	}

}
