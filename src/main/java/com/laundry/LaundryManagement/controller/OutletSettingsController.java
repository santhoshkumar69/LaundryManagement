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

import com.laundry.LaundryManagement.dto.OutletSettingsDto;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.service.RegistrationService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("outletSettings")
public class OutletSettingsController {

	@Autowired
	private RegistrationService registrationService;

	@GetMapping("/get")
	public ResponseEntity<Object> get(@RequestParam("userId") String userId,
			@RequestParam(value = "outletId", required = false) Integer outletId) {
		try {
			return ResponseEntity.ok(registrationService.getOutletSettings(userId, outletId));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(new ReturnResponse(ex.getMessage(), 400));
		}
	}

	@PostMapping("/update")
	public ResponseEntity<Object> update(@RequestBody OutletSettingsDto settings) {
		try {
			return ResponseEntity.ok(registrationService.updateOutletSettings(settings));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(new ReturnResponse(ex.getMessage(), 400));
		}
	}
}
