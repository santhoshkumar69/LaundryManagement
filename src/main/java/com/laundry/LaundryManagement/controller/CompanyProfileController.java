package com.laundry.LaundryManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.dto.CompanyProfileDto;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.service.RegistrationService;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("companyProfile")
public class CompanyProfileController {

	@Autowired
	private RegistrationService registrationService;

	@GetMapping("/get")
	public ResponseEntity<Object> get(@RequestParam("userId") Long userId) {
		try {
			return ResponseEntity.ok(registrationService.getCompanyProfile(userId));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(new ReturnResponse(ex.getMessage(), 400));
		}
	}

	@PostMapping("/update")
	public ResponseEntity<Object> update(@RequestBody CompanyProfileDto profile) {
		try {
			return ResponseEntity.ok(registrationService.updateCompanyProfile(profile));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(new ReturnResponse(ex.getMessage(), 400));
		}
	}
}
