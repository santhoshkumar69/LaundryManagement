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

import com.laundry.LaundryManagement.auth.Permissions;
import com.laundry.LaundryManagement.auth.RolePermissions;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.service.ExpenseService;

/** Accounts > Expense Track for the current outlet. */
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("expenses")
public class ExpenseController {

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private RolePermissions rolePermissions;

	@GetMapping("/categories")
	public ResponseEntity<Object> categories() {
		return ResponseEntity.ok(ExpenseService.CATEGORIES);
	}

	@GetMapping("/list")
	public ResponseEntity<Object> list(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "category", required = false) String category) {
		return ResponseEntity.ok(expenseService.list(from, to, category));
	}

	@PostMapping("/save")
	public ResponseEntity<Object> save(@RequestBody ExpenseService.SaveExpenseRequest request) {
		rolePermissions.require("EXPENSES",
				request == null || request.id() == null ? Permissions.Action.CREATE : Permissions.Action.EDIT);
		return ResponseEntity.ok(expenseService.save(request));
	}

	@PostMapping("/delete")
	public ResponseEntity<Object> delete(@RequestParam("id") Long id) {
		expenseService.delete(id);
		return ResponseEntity.ok(new ReturnResponse("Expense deleted", 200));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> invalid(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(new ReturnResponse(ex.getMessage(), 400));
	}
}
