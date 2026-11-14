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
import com.laundry.LaundryManagement.service.ExpenseCategoryService;

/** Accounts & Reports > Expense Categories for the current outlet. */
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("expenseCategories")
public class ExpenseCategoryController {

	private static final String SCREEN = "EXPENSE_CATEGORIES";

	@Autowired
	private ExpenseCategoryService categoryService;

	@Autowired
	private RolePermissions rolePermissions;

	@GetMapping("/list")
	public ResponseEntity<Object> list(@RequestParam(value = "includeDeleted", defaultValue = "false") boolean includeDeleted) {
		return ResponseEntity.ok(categoryService.list(includeDeleted));
	}

	@PostMapping("/saveCategory")
	public ResponseEntity<Object> saveCategory(@RequestBody ExpenseCategoryService.SaveCategoryRequest request) {
		rolePermissions.require(SCREEN, request == null || request.id() == null ? Permissions.Action.CREATE : Permissions.Action.EDIT);
		return ResponseEntity.ok(categoryService.saveCategory(request));
	}

	@PostMapping("/deleteCategory")
	public ResponseEntity<Object> deleteCategory(@RequestParam("id") Long id) {
		categoryService.deleteCategory(id);
		return ResponseEntity.ok(new ReturnResponse("Category deleted", 200));
	}

	@PostMapping("/restoreCategory")
	public ResponseEntity<Object> restoreCategory(@RequestParam("id") Long id) {
		categoryService.restoreCategory(id);
		return ResponseEntity.ok(new ReturnResponse("Category restored", 200));
	}

	@PostMapping("/saveSubcategory")
	public ResponseEntity<Object> saveSubcategory(@RequestBody ExpenseCategoryService.SaveSubcategoryRequest request) {
		rolePermissions.require(SCREEN, request == null || request.id() == null ? Permissions.Action.CREATE : Permissions.Action.EDIT);
		return ResponseEntity.ok(categoryService.saveSubcategory(request));
	}

	@PostMapping("/deleteSubcategory")
	public ResponseEntity<Object> deleteSubcategory(@RequestParam("id") Long id) {
		categoryService.deleteSubcategory(id);
		return ResponseEntity.ok(new ReturnResponse("Subcategory deleted", 200));
	}

	@PostMapping("/restoreSubcategory")
	public ResponseEntity<Object> restoreSubcategory(@RequestParam("id") Long id) {
		categoryService.restoreSubcategory(id);
		return ResponseEntity.ok(new ReturnResponse("Subcategory restored", 200));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> invalid(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(new ReturnResponse(ex.getMessage(), 400));
	}
}
