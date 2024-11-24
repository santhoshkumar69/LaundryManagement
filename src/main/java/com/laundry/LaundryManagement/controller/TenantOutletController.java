package com.laundry.LaundryManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.constant.AppStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.dto.TenantAndOutletDetailsDTO;
import com.laundry.LaundryManagement.model.OutletDetails;
import com.laundry.LaundryManagement.model.TenantDetails;
import com.laundry.LaundryManagement.service.TenantAndOutletService;

@RestController
@RequestMapping("TenantAndOutlet")
public class TenantOutletController {

	
	@Autowired
	TenantAndOutletService tenantAndOutletService;
	
	
	@GetMapping("/getAllTenantAndOutlet")
	public ResponseEntity<Object> getAllTenantAndOutlet() {
		try {
			List<TenantDetails> tenantDetails = tenantAndOutletService.getAllTenantDetails();
			return ResponseEntity.ok(tenantDetails);
		} catch (Exception e) {
			return	ResponseEntity.status(500).body(new ReturnResponse(AppStatus.SOME_ERROR_OCCURRED,AppStatusCode.INTERNAL_ERROR));
		}
	}
	
	@GetMapping("/getTenantAndOutlet")
	public ResponseEntity<Object> getTenantAndOtlet(String tenantCode) {
		try {
			TenantDetails tenantDetails = tenantAndOutletService.getTenantDetails(tenantCode);
			return ResponseEntity.ok(tenantDetails);
		} catch (Exception e) {
			return	ResponseEntity.status(500).body(new ReturnResponse(AppStatus.SOME_ERROR_OCCURRED,AppStatusCode.INTERNAL_ERROR));
		}
	}
	
	@PostMapping("/createTenantAndOutlet")
	public ResponseEntity<Object> createTenantAndOutlet(TenantAndOutletDetailsDTO tenantAndOutletDetailsDTO) {
		try {
			TenantDetails tenantDetails = tenantAndOutletService.createTenantDetails(tenantAndOutletDetailsDTO);
			return ResponseEntity.ok(tenantDetails);
		} catch (Exception e) {
			return	ResponseEntity.status(500).body(new ReturnResponse(AppStatus.SOME_ERROR_OCCURRED,AppStatusCode.INTERNAL_ERROR));
		}
		 
	}
	
	@PostMapping("/updateTenantAndOutlet")
	public ResponseEntity<Object> updateTenantAndOutlet(TenantAndOutletDetailsDTO tenantAndOutletDetailsDTO) {
		try {
			TenantDetails tenantDetails = tenantAndOutletService.updateTenantDetails(tenantAndOutletDetailsDTO);
			return ResponseEntity.ok(tenantDetails);
		} catch (Exception e) {
			return	ResponseEntity.status(500).body(new ReturnResponse(AppStatus.SOME_ERROR_OCCURRED,AppStatusCode.INTERNAL_ERROR));
		}
		
	}
	
	@DeleteMapping("/deleteOutlet")
	public ResponseEntity<Object> deleteOutlet(List<OutletDetails> outletDetailsList) {
		try {
			tenantAndOutletService.deleteOutletFromTenant(outletDetailsList);
			return ResponseEntity.ok("Deleted Outlet Successfuly");
		} catch (Exception e) {
			return	ResponseEntity.status(500).body(new ReturnResponse(AppStatus.SOME_ERROR_OCCURRED,AppStatusCode.INTERNAL_ERROR));
		}
		
	}
			
	@DeleteMapping("/deleteTenant")
	public ResponseEntity<Object> deleteTenant(List<TenantDetails> tenantDetailsList) {
		try {
			tenantAndOutletService.deleteTenant(tenantDetailsList);
			return ResponseEntity.ok("Tenant deleted successfully");
		}  catch (Exception e) {
			return	ResponseEntity.status(500).body(new ReturnResponse(AppStatus.SOME_ERROR_OCCURRED,AppStatusCode.INTERNAL_ERROR));
		}
		
	}
}
