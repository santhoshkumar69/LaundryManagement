package com.laundry.LaundryManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.service.OutletAdminService;
import com.laundry.LaundryManagement.service.UserAdminService;

/** Outlets and users of the signed-in admin's tenant. */
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("admin")
public class TenantAdminController {

	@Autowired
	private OutletAdminService outletAdminService;

	@Autowired
	private UserAdminService userAdminService;

	@GetMapping("/outlets")
	public ResponseEntity<Object> outlets() {
		return ResponseEntity.ok(outletAdminService.list());
	}

	@PostMapping("/outlets")
	public ResponseEntity<Object> createOutlet(@RequestBody OutletAdminService.CreateOutletRequest request) {
		return ResponseEntity.ok(outletAdminService.create(request));
	}

	@GetMapping("/users")
	public ResponseEntity<Object> users() {
		return ResponseEntity.ok(userAdminService.list());
	}

	@GetMapping("/roles")
	public ResponseEntity<Object> roles() {
		return ResponseEntity.ok(userAdminService.roles());
	}

	@PostMapping("/users")
	public ResponseEntity<Object> saveUser(@RequestBody UserAdminService.SaveUserRequest request) {
		return ResponseEntity.ok(userAdminService.save(request));
	}

	@PostMapping("/users/active")
	public ResponseEntity<Object> setActive(@RequestParam("id") Integer id, @RequestParam("active") boolean active) {
		userAdminService.setActive(id, active);
		return ResponseEntity.ok(new ReturnResponse(active ? "User activated" : "User deactivated", 200));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> invalid(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(new ReturnResponse(ex.getMessage(), 400));
	}
}
