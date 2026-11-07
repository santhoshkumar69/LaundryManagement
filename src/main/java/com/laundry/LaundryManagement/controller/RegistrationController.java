package com.laundry.LaundryManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.dto.RegistrationRequest;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.service.RegistrationService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("registration")
public class RegistrationController {

	@Autowired
	private RegistrationService registrationService;

	@PostMapping("/register")
	public ResponseEntity<Object> register(@RequestBody RegistrationRequest request) {
		try {
			return ResponseEntity.ok(registrationService.register(request));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(new ReturnResponse(ex.getMessage(), 400));
		}
	}
}
