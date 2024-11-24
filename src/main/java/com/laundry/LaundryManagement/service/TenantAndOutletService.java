package com.laundry.LaundryManagement.service;

import java.util.List;

import com.laundry.LaundryManagement.dto.TenantAndOutletDetailsDTO;
import com.laundry.LaundryManagement.model.OutletDetails;
import com.laundry.LaundryManagement.model.TenantDetails;

public interface TenantAndOutletService {
	
	List<TenantDetails> getAllTenantDetails() throws Exception;
	
	TenantDetails getTenantDetails(String tenantCode) throws Exception;

	TenantDetails createTenantDetails(TenantAndOutletDetailsDTO tenantAndOutletDetailsDTO) throws Exception;
	
	TenantDetails updateTenantDetails(TenantAndOutletDetailsDTO tenantAndOutletDetailsDTO) throws Exception;
	
	void deleteOutletFromTenant(List<OutletDetails> outletDetails) throws Exception;
	
	void deleteTenant(List<TenantDetails> tenantDetails) throws Exception;
}
