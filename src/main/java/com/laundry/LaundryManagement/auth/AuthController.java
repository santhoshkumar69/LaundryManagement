package com.laundry.LaundryManagement.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.dto.ReturnResponse;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("auth")
public class AuthController {

	public record LoginRequest(String tenantCode, Long loginId, String password) {
	}

	public record SelectOutletRequest(Integer outletId) {
	}

	@Autowired
	private AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request.tenantCode(), request.loginId(), request.password()));
	}

	@GetMapping("/outlets")
	public ResponseEntity<Object> outlets() {
		return ResponseEntity.ok(authService.outletsForCurrentUser());
	}

	@PostMapping("/selectOutlet")
	public ResponseEntity<Object> selectOutlet(@RequestBody SelectOutletRequest request) {
		return ResponseEntity.ok(authService.selectOutlet(request.outletId()));
	}

	@GetMapping("/me")
	public ResponseEntity<Object> me() {
		return ResponseEntity.ok(authService.current());
	}

	@ExceptionHandler(AuthService.AuthException.class)
	public ResponseEntity<Object> authFailed(AuthService.AuthException ex) {
		return ResponseEntity.status(HttpStatus.valueOf(ex.getStatus())).body(new ReturnResponse(ex.getMessage(), ex.getStatus()));
	}
}
