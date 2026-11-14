package com.laundry.LaundryManagement.controller;

import jakarta.servlet.http.HttpSession;

import com.laundry.LaundryManagement.auth.Permissions;
import com.laundry.LaundryManagement.auth.RolePermissions;
import com.laundry.LaundryManagement.constant.AppStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.model.PaymentMode;
import com.laundry.LaundryManagement.service.PaymentModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.repository.PaymentModeRepo;


@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("payment")
public class PaymentModeController {
	

	
	@Autowired
    PaymentModeService paymentModeService;
	
	@Autowired
	PaymentModeRepo paymentModeRepo;

	@Autowired
	private RolePermissions rolePermissions;

	@PostMapping("/savePaymentMode")
	public ResponseEntity<Object> createItemDetails(@RequestBody PaymentMode paymentMode, HttpSession httpSession) {
		rolePermissions.require("PAYMENT_MODES",
				paymentMode.getId() == null ? Permissions.Action.CREATE : Permissions.Action.EDIT);
		String name = paymentMode.getPaymentMode() == null ? "" : paymentMode.getPaymentMode().trim();
		if (name.isEmpty()) {
			return ResponseEntity.badRequest().body(new ReturnResponse("Payment mode name is required", 400));
		}
		boolean taken = paymentModeRepo.findAll().stream()
				.anyMatch(m -> m.getPaymentMode().equalsIgnoreCase(name) && !m.getId().equals(paymentMode.getId()));
		if (taken) {
			return ResponseEntity.badRequest()
					.body(new ReturnResponse("A payment mode named " + name + " already exists in this outlet", 400));
		}
		try {
			boolean isSaved = paymentModeService.createPaymentMode(paymentMode);
			if(isSaved) {
				return ResponseEntity.ok().body(paymentModeService.findAll());
			}
			return ResponseEntity.status(500)
					.body(new ReturnResponse(AppStatus.UNABLE_TO_SAVE, AppStatusCode.INTERNAL_ERROR));
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500)
					.body(new ReturnResponse(AppStatus.UNABLE_TO_SAVE, AppStatusCode.INTERNAL_ERROR));
		}
	}
	
	@GetMapping("/getPaymentMode")
	public ResponseEntity<Object> getPaymentMode() {
		return ResponseEntity.ok().body(paymentModeService.findAll());
	}
	
	@GetMapping("/getInactivePaymentMode")
	public ResponseEntity<Object> findInactivePaymentMode() {
		return ResponseEntity.ok().body(paymentModeService.findInactivePaymentMode());
	}

	@PostMapping("/activatePaymentMode")
	public ResponseEntity<Object> activatePaymentMode(@RequestParam Long[] ids) {
		return ResponseEntity.ok().body(paymentModeService.activatePaymentMode(ids));
	}
	

	@PostMapping("/deActivatePaymentMode")
	public ResponseEntity<Object> deActivatePaymentMode(Long[] ids) {
		return ResponseEntity.ok().body(paymentModeService.deActivatePaymentMode(ids));
	}
	
	

}
