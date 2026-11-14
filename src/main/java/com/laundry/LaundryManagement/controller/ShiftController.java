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
import com.laundry.LaundryManagement.service.ShiftService;

/** Cashier shifts: the caller's own shift, and the Accounts > Shifts report for supervisors. */
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("shifts")
public class ShiftController {

	@Autowired
	private ShiftService shiftService;

	@GetMapping("/current")
	public ResponseEntity<Object> current() {
		return ResponseEntity.ok(shiftService.current());
	}

	@PostMapping("/start")
	public ResponseEntity<Object> start(@RequestBody ShiftService.StartShiftRequest request) {
		return ResponseEntity.ok(shiftService.start(request));
	}

	@PostMapping("/end")
	public ResponseEntity<Object> end(@RequestBody ShiftService.EndShiftRequest request) {
		return ResponseEntity.ok(shiftService.end(request));
	}

	@GetMapping("/report")
	public ResponseEntity<Object> report(@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "userDbId", required = false) Integer userDbId) {
		return ResponseEntity.ok(shiftService.report(from, to, userDbId));
	}

	@GetMapping("/cashiers")
	public ResponseEntity<Object> cashiers() {
		return ResponseEntity.ok(shiftService.cashiers());
	}

	@PostMapping("/forceEnd")
	public ResponseEntity<Object> forceEnd(@RequestBody ShiftService.EndShiftRequest request) {
		return ResponseEntity.ok(shiftService.forceEnd(request));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> invalid(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(new ReturnResponse(ex.getMessage(), 400));
	}
}
