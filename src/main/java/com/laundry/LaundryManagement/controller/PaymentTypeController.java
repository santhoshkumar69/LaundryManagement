package com.laundry.LaundryManagement.controller;

import javax.servlet.http.HttpSession;

import com.laundry.LaundryManagement.constant.AppStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.model.PaymentType;
import com.laundry.LaundryManagement.service.PaymentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.repository.PaymentTypeRepo;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("payment")
public class PaymentTypeController {
	
	@Autowired
    PaymentTypeService paymentTypeService;
	
	@Autowired
	PaymentTypeRepo paymentTypeRepo;

	@PostMapping("/savePaymentType")
	public ResponseEntity<Object> createItemDetails(@RequestBody PaymentType paymentType, HttpSession httpSession) {
		try {
			boolean isSaved = false;
			if(paymentType.getPaymentType() != null) {
				String chargeType = paymentType.getPaymentType();
				PaymentType aPaymentType = paymentTypeRepo.findByPaymentType(chargeType);
				if(aPaymentType != null && paymentType.getId() == null) {
					return ResponseEntity.status(500)
							.body(new ReturnResponse(AppStatus.ALREADY_EXIST, AppStatusCode.INTERNAL_ERROR));
				} else {
					isSaved = paymentTypeService.createPaymentType(paymentType);
				}
			}
			if(isSaved) {
				return ResponseEntity.ok().body(paymentTypeService.findAll());
			}
			return ResponseEntity.status(500)
					.body(new ReturnResponse(AppStatus.UNABLE_TO_SAVE, AppStatusCode.INTERNAL_ERROR));
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500)
					.body(new ReturnResponse(AppStatus.UNABLE_TO_SAVE, AppStatusCode.INTERNAL_ERROR));
		}
	}
	
	@GetMapping("/getPaymentType")
	public ResponseEntity<Object> getPaymentType() {
		return ResponseEntity.ok().body(paymentTypeService.findAll());
	}
	
	@GetMapping("/getInactivePaymentType")
	public ResponseEntity<Object> findInactivePaymentType() {
		return ResponseEntity.ok().body(paymentTypeService.findInactivePaymentType());
	}

	@PostMapping("/activatePaymentType")
	public ResponseEntity<Object> activatePaymentType(@RequestParam Long[] ids) {
		return ResponseEntity.ok().body(paymentTypeService.activatePaymentType(ids));
	}
	

	@PostMapping("/deActivatePaymentType")
	public ResponseEntity<Object> deActivatePaymentType(Long[] ids) {
		return ResponseEntity.ok().body(paymentTypeService.deActivatePaymentType(ids));
	}
	
	
}
