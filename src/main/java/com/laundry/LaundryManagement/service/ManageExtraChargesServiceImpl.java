package com.laundry.LaundryManagement.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.model.ExtraCharges;
import com.laundry.LaundryManagement.repository.ManageExtraChargesRepo;

@Service
public class ManageExtraChargesServiceImpl implements ManageExtraChargesService {
	
	@Autowired
	private ManageExtraChargesRepo manageExtraChargesRepo;

	@Override
	public boolean createExtraCharge(ExtraCharges extraCharges) throws Exception {
		try {
			extraCharges.setActiveStatus(1);
			if (extraCharges.getId() == null) {
				extraCharges.setCreatedDate(new Date());
			} else {
				extraCharges.setUpdatedDate(new Date());
			}
			manageExtraChargesRepo.save(extraCharges);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public List<ExtraCharges> findAll() {
		return manageExtraChargesRepo.findAll();
	}

	@Override
	public List<ExtraCharges> activateExtraCharge(Long[] ids) {
		List<ExtraCharges> theExtraCharges = new ArrayList<ExtraCharges>();
		for(Long id : ids) {
			ExtraCharges extraCharges = manageExtraChargesRepo.findById(id).orElse(null);
			if(extraCharges != null) {
				extraCharges.setActiveStatus(1);
				manageExtraChargesRepo.save(extraCharges);
				theExtraCharges.add(extraCharges);
			}
		}
		return theExtraCharges;
	}

	@Override
	public List<ExtraCharges> deActivateExtraCharge(Long[] ids) {
		List<ExtraCharges> theExtraCharges = new ArrayList<ExtraCharges>();
		for(Long id : ids) {
			ExtraCharges extraCharges = manageExtraChargesRepo.findById(id).orElse(null);
			if(extraCharges != null) {
				extraCharges.setActiveStatus(0);
				manageExtraChargesRepo.save(extraCharges);
				
			}
		}
		theExtraCharges = this.findActiveExtraCharges();
		return theExtraCharges;
	}

	@Override
	public List<ExtraCharges> findInactiveExtraCharges() {
		return manageExtraChargesRepo.findInactiveExtraCharge();
	}

	@Override
	public List<ExtraCharges> findActiveExtraCharges() {
		return manageExtraChargesRepo.findActiveExtraCharges();
	}
	
	
}
