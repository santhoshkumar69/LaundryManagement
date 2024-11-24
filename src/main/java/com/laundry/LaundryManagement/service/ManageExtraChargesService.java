package com.laundry.LaundryManagement.service;

import java.util.List;

import com.laundry.LaundryManagement.model.ExtraCharges;

public interface ManageExtraChargesService {

	public boolean createExtraCharge(ExtraCharges extraCharges) throws Exception;
	
	public List<ExtraCharges> findAll();
	
	public List<ExtraCharges> findInactiveExtraCharges();
	
	public List<ExtraCharges> activateExtraCharge(Long[] ids);
	
	public List<ExtraCharges> deActivateExtraCharge(Long[] ids);
	
	public List<ExtraCharges> findActiveExtraCharges();
}
