package com.laundry.LaundryManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.constant.AppStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ChangePasswordRequest;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.service.LoginService;

/** Signing in now happens at /auth/login (with tenant ID); this controller keeps password changes. */
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("login")
public class LoginController {

	@Autowired
	LoginService loginService;

	@PostMapping(value = "/changePassword")
	public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordRequest request) {
		try {
			loginService.changePassword(request, RequestScope.globalInfo());
			return ResponseEntity.ok(new ReturnResponse(AppStatus.PASSWORD_UPDATE, AppStatusCode.OK));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(500).body(new ReturnResponse(ex.getMessage(), AppStatusCode.INTERNAL_ERROR));
		}
	}
}
