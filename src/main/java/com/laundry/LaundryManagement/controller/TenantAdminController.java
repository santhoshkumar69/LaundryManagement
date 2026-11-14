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

import com.laundry.LaundryManagement.auth.DeletedOutlets;
import com.laundry.LaundryManagement.auth.RolePermissions;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.service.OutletAdminService;
import com.laundry.LaundryManagement.service.RoleAdminService;
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

	@Autowired
	private DeletedOutlets deletedOutlets;

	@Autowired
	private RoleAdminService roleAdminService;

	@Autowired
	private RolePermissions rolePermissions;

	@GetMapping("/outlets")
	public ResponseEntity<Object> outlets(
			@RequestParam(value = "includeDeleted", defaultValue = "false") boolean includeDeleted) {
		return ResponseEntity.ok(outletAdminService.list(includeDeleted));
	}

	@PostMapping("/outlets")
	public ResponseEntity<Object> createOutlet(@RequestBody OutletAdminService.CreateOutletRequest request) {
		return ResponseEntity.ok(outletAdminService.create(request));
	}

	@PostMapping("/outlets/delete")
	public ResponseEntity<Object> deleteOutlet(@RequestParam("id") Integer id) {
		OutletAdminService.OutletSummary deleted = outletAdminService.delete(id);
		deletedOutlets.refresh();
		return ResponseEntity.ok(deleted);
	}

	@PostMapping("/outlets/invoiceTemplate")
	public ResponseEntity<Object> applyInvoiceTemplate(@RequestParam("id") Integer id,
			@RequestParam("templateId") Integer templateId) {
		return ResponseEntity.ok(outletAdminService.applyInvoiceTemplate(id, templateId));
	}

	@PostMapping("/outlets/restore")
	public ResponseEntity<Object> restoreOutlet(@RequestParam("id") Integer id) {
		OutletAdminService.OutletSummary restored = outletAdminService.restore(id);
		deletedOutlets.refresh();
		return ResponseEntity.ok(restored);
	}

	@GetMapping("/users")
	public ResponseEntity<Object> users() {
		return ResponseEntity.ok(userAdminService.list());
	}

	@GetMapping("/roles")
	public ResponseEntity<Object> roles() {
		return ResponseEntity.ok(userAdminService.roles());
	}

	@GetMapping("/roles/catalog")
	public ResponseEntity<Object> permissionCatalog() {
		return ResponseEntity.ok(roleAdminService.catalog());
	}

	@GetMapping("/roles/details")
	public ResponseEntity<Object> roleDetails() {
		return ResponseEntity.ok(roleAdminService.list());
	}

	@PostMapping("/roles/save")
	public ResponseEntity<Object> saveRole(@RequestBody RoleAdminService.SaveRoleRequest request) {
		RoleAdminService.RoleSummary saved = roleAdminService.save(request);
		rolePermissions.refresh();
		return ResponseEntity.ok(saved);
	}

	@PostMapping("/roles/delete")
	public ResponseEntity<Object> deleteRole(@RequestParam("id") Integer id) {
		roleAdminService.delete(id);
		rolePermissions.refresh();
		return ResponseEntity.ok(roleAdminService.list());
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
