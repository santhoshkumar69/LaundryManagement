package com.laundry.LaundryManagement.controller;

import javax.servlet.http.HttpSession;

import com.laundry.LaundryManagement.constant.AppStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.service.ManageExtraChargesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.model.ExtraCharges;
import com.laundry.LaundryManagement.repository.ManageExtraChargesRepo;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("manage")
public class ManageExtraChargesController {
	
	@Autowired
    ManageExtraChargesService manageExtraChargesService;
	
	@Autowired
	ManageExtraChargesRepo manageExtraChargesRepo;

	@PostMapping("/saveExtraCharges")
	public ResponseEntity<Object> createItemDetails(@RequestBody ExtraCharges extraCharges, HttpSession httpSession) {
		try {
			boolean isSaved = false;
			if(extraCharges.getChargeName() != null) {
				String chargeName = extraCharges.getChargeName();
				ExtraCharges aExtraCharges = manageExtraChargesRepo.findByChargeName(chargeName);
				if(aExtraCharges != null && extraCharges.getId() == null) {
					return ResponseEntity.status(500)
							.body(new ReturnResponse(AppStatus.ALREADY_EXIST, AppStatusCode.INTERNAL_ERROR));
				} else {
					isSaved = manageExtraChargesService.createExtraCharge(extraCharges);
				}
			}
			if(isSaved) {
				return ResponseEntity.ok().body(manageExtraChargesService.findAll());
			}
			return ResponseEntity.status(500)
					.body(new ReturnResponse(AppStatus.UNABLE_TO_SAVE, AppStatusCode.INTERNAL_ERROR));
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500)
					.body(new ReturnResponse(AppStatus.UNABLE_TO_SAVE, AppStatusCode.INTERNAL_ERROR));
		}
	}
	
	@GetMapping("/getExtraCharges")
	public ResponseEntity<Object> getExtraCharges() {
		return ResponseEntity.ok().body(manageExtraChargesService.findActiveExtraCharges());
	}
	
	@GetMapping("/getInactiveExtraCharges")
	public ResponseEntity<Object> findInactiveExtraCharges() {
		return ResponseEntity.ok().body(manageExtraChargesService.findInactiveExtraCharges());
	}

	@PostMapping("/activateExtraCharges")
	public ResponseEntity<Object> activateExtraCharges(@RequestParam Long[] ids) {
		return ResponseEntity.ok().body(manageExtraChargesService.activateExtraCharge(ids));
	}
	

	@PostMapping("/deActivateExtraCharges")
	public ResponseEntity<Object> deActivateExtraCharges(Long[] ids) {
		return ResponseEntity.ok().body(manageExtraChargesService.deActivateExtraCharge(ids));
	}
	
}
