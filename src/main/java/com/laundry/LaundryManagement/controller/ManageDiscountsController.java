package com.laundry.LaundryManagement.controller;

import javax.servlet.http.HttpSession;

import com.laundry.LaundryManagement.constant.AppStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.model.Discounts;
import com.laundry.LaundryManagement.service.ManageDiscountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.repository.ManageDiscountsRepo;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("manage")
public class ManageDiscountsController {


	@Autowired
    ManageDiscountsService manageDiscountsService;
	
	@Autowired
	ManageDiscountsRepo manageDiscountsRepo;

	@PostMapping("/saveDiscounts")
	public ResponseEntity<Object> createItemDetails(@RequestBody Discounts discounts, HttpSession httpSession) {
		try {
			boolean isSaved = false;
			if(discounts.getDiscountName() != null) {
				String chargeName = discounts.getDiscountName();
				Discounts aDiscounts = manageDiscountsRepo.findByDiscountName(chargeName);
				if(aDiscounts != null && discounts.getId() == null) {
					return ResponseEntity.status(500)
							.body(new ReturnResponse(AppStatus.ALREADY_EXIST, AppStatusCode.INTERNAL_ERROR));
				} else {
					isSaved = manageDiscountsService.createDiscounts(discounts);
				}
			}
			if(isSaved) {
				return ResponseEntity.ok().body(manageDiscountsService.findAll());
			}
			return ResponseEntity.status(500)
					.body(new ReturnResponse(AppStatus.UNABLE_TO_SAVE, AppStatusCode.INTERNAL_ERROR));
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500)
					.body(new ReturnResponse(AppStatus.UNABLE_TO_SAVE, AppStatusCode.INTERNAL_ERROR));
		}
	}
	
	@GetMapping("/getDiscounts")
	public ResponseEntity<Object> getDiscounts() {
		return ResponseEntity.ok().body(manageDiscountsService.findActiveDiscounts());
	}
	
	@GetMapping("/getInactiveDiscounts")
	public ResponseEntity<Object> findInactiveDiscounts() {
		return ResponseEntity.ok().body(manageDiscountsService.findInactiveDiscounts());
	}

	@PostMapping("/activateDiscounts")
	public ResponseEntity<Object> activateDiscounts(@RequestParam Long[] ids) {
		return ResponseEntity.ok().body(manageDiscountsService.activateDiscounts(ids));
	}
	

	@PostMapping("/deActivateDiscounts")
	public ResponseEntity<Object> deActivateDiscounts(Long[] ids) {
		return ResponseEntity.ok().body(manageDiscountsService.deActivateDiscounts(ids));
	}
	
	
}
