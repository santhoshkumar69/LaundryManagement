package com.laundry.LaundryManagement.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.service.ShiftRequiredException;

@RestControllerAdvice
public class PermissionDeniedHandler {

	@ExceptionHandler(RolePermissions.PermissionDeniedException.class)
	public ResponseEntity<Object> denied(RolePermissions.PermissionDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ReturnResponse(ex.getMessage(), 403));
	}

	@ExceptionHandler(ShiftRequiredException.class)
	public ResponseEntity<Object> shiftRequired(ShiftRequiredException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ReturnResponse(ex.getMessage(), 409));
	}
}
