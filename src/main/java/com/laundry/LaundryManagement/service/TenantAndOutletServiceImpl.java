package com.laundry.LaundryManagement.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.dto.OutletDetailsDTO;
import com.laundry.LaundryManagement.dto.TenantAndOutletDetailsDTO;
import com.laundry.LaundryManagement.model.OutletDetails;
import com.laundry.LaundryManagement.model.TenantDetails;
import com.laundry.LaundryManagement.repository.OutletDetailsRepo;
import com.laundry.LaundryManagement.repository.TenantDetailsRepo;

@Service
public class TenantAndOutletServiceImpl implements TenantAndOutletService {

	
	@Autowired
	private TenantDetailsRepo tenantDetailsRepo;
	
	@Autowired
	private OutletDetailsRepo outletDetailsRepo;
	
	@Override
	public List<TenantDetails> getAllTenantDetails() {
		List<TenantDetails> tenantDetailsList = tenantDetailsRepo.findAll();
		
		return tenantDetailsList;
	}

	@Override
	public TenantDetails getTenantDetails(String tenantCode) {
		return tenantDetailsRepo.findTenantDetails(tenantCode);
	}

	@Override
	public TenantDetails createTenantDetails(TenantAndOutletDetailsDTO tenantAndOutletDetailsDTO) throws Exception {

		TenantDetails tenantDetails = new TenantDetails();
		tenantDetails.setTenantCode(tenantAndOutletDetailsDTO.getTenantCode());
		tenantDetails.setAddress(tenantAndOutletDetailsDTO.getAddress());
		tenantDetails.setCompanyName(tenantAndOutletDetailsDTO.getCompanyName());
		tenantDetails.setContactName(tenantAndOutletDetailsDTO.getContactName());
		tenantDetails.setEmailId(tenantAndOutletDetailsDTO.getEmailId());
		tenantDetails.setNoOfOutlets(tenantAndOutletDetailsDTO.getNoOfOutlets());
		tenantDetails.setSubscription(tenantAndOutletDetailsDTO.getSubscription());
		
		
		tenantDetailsRepo.save(tenantDetails);
		
		TenantDetails persistedTenantDetails = tenantDetailsRepo.findTenantDetails(tenantDetails.getTenantCode());
		
		List<OutletDetails> outletDetailsList = new ArrayList<>();
		
		for(OutletDetailsDTO outletDetailsDto : tenantAndOutletDetailsDTO.getTheOutLetDetailsDTO()) {
			OutletDetails outletDetails = new OutletDetails();
			outletDetails.setOutletCode(outletDetailsDto.getOutletCode());
			outletDetails.setOutletName(outletDetailsDto.getOutletName());
			outletDetails.setTenantId(persistedTenantDetails.getId());
			
			outletDetailsList.add(outletDetails);
			
		}
		
		outletDetailsRepo.saveAll(outletDetailsList);
		
		return persistedTenantDetails;
	}

	@Override
	public TenantDetails updateTenantDetails(TenantAndOutletDetailsDTO tenantAndOutletDetailsDTO) {
		
		TenantDetails tenantDetails = tenantDetailsRepo.findTenantDetails(tenantAndOutletDetailsDTO.getTenantCode());
		
		tenantDetails.setTenantCode(tenantAndOutletDetailsDTO.getTenantCode());
		tenantDetails.setAddress(tenantAndOutletDetailsDTO.getAddress());
		tenantDetails.setCompanyName(tenantAndOutletDetailsDTO.getCompanyName());
		tenantDetails.setContactName(tenantAndOutletDetailsDTO.getContactName());
		tenantDetails.setEmailId(tenantAndOutletDetailsDTO.getEmailId());
		tenantDetails.setNoOfOutlets(tenantAndOutletDetailsDTO.getNoOfOutlets());
		tenantDetails.setSubscription(tenantAndOutletDetailsDTO.getSubscription());
		
		tenantDetailsRepo.save(tenantDetails);

		List<OutletDetails> outletDetailsList = new ArrayList<>();
		
		for(OutletDetailsDTO outletDetailsDto : tenantAndOutletDetailsDTO.getTheOutLetDetailsDTO()) {
			OutletDetails outletDetails = new OutletDetails();
			outletDetails.setOutletCode(outletDetailsDto.getOutletCode());
			outletDetails.setOutletName(outletDetailsDto.getOutletName());
			outletDetails.setTenantId(tenantDetails.getId());
			
			outletDetailsList.add(outletDetails);
			
		}
		
		outletDetailsRepo.saveAll(outletDetailsList);
		
		return tenantDetails;
	}

	@Override
	public void deleteOutletFromTenant(List<OutletDetails> outletDetails) {
		outletDetailsRepo.deleteAll(outletDetails);
	}

	@Override
	public void deleteTenant(List<TenantDetails> tenantDetails) {
		tenantDetailsRepo.deleteAll(tenantDetails);
	}
	
}
