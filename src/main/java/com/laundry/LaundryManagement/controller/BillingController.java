package com.laundry.LaundryManagement.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.laundry.LaundryManagement.constant.AppConstants;
import com.laundry.LaundryManagement.model.CustomerInfo;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.service.BillingService;
import com.laundry.LaundryManagement.service.MasterService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.dto.BilledDetails;
import com.laundry.LaundryManagement.dto.BillingInfos;
import com.laundry.LaundryManagement.dto.ConfirmBilling;
import com.laundry.LaundryManagement.dto.ErrorResponse;
import com.laundry.LaundryManagement.dto.ReceiptDetailDto;
import com.laundry.LaundryManagement.beans.ItemMasterBean;


/**
 * @author ayyammal
 *
 *         26-Dec-2018
 */
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("billing")
public class BillingController {

	@Autowired
    MasterService masterService;
	
	@Autowired
    BillingService billingService;
	
	@GetMapping("/getBillingComponent")
	public ResponseEntity<?> getBillingComponent(){
		List<ItemMasterBean> components = masterService.fetchItemDetails();
		if(!CollectionUtils.isEmpty(components)){
			return ResponseEntity.ok().body(components);
		}else return ResponseEntity.status(HttpStatus.NO_CONTENT.value()).body(new ErrorResponse(HttpStatus.NO_CONTENT.getReasonPhrase(),HttpStatus.NO_CONTENT.value()));
	}
	
	@PostMapping("getCustomer")
	public ResponseEntity<?> getCustomerDetails(@RequestParam("cusID") Integer cusID, @RequestParam("mobileNo") String mobileNo){
		CustomerInfo cusInfo = billingService.fetchCustomerDetails(cusID,mobileNo);
		return ResponseEntity.ok().body(cusInfo);
	}
	
	@PostMapping("/createEstimation")
	public ResponseEntity<?> createBillingComponents(@RequestBody BillingInfos billingReq, HttpSession httpSession){
		BilledDetails billedCompos = billingService.createEstimationDetails(billingReq,(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
		return ResponseEntity.ok().body(billedCompos);
	}
	
	@PostMapping("/confirmBilling")
	public ResponseEntity<?> confirmBillingDetails(@RequestBody ConfirmBilling confirmBill, HttpSession httpSession) {
		ReceiptDetailDto receiptInfo = billingService.confirmBillingRequest(confirmBill, (GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
		return ResponseEntity.ok().body(receiptInfo);
	}
	
	@PostMapping("/cancelBilling")
	public ResponseEntity<?> cancelBillingReqest(@RequestBody ConfirmBilling confirmBill, HttpSession httpSession) {
		billingService.cancelBillingRequest(confirmBill, (GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
		return ResponseEntity.ok().body("Cancelled successfully");
	}
	
	@PostMapping("/removeItem")
	public ResponseEntity<?> removeBillItem(@RequestParam("itemID") int itemID) {
		billingService.removeBillItems(itemID);
		return ResponseEntity.ok().body("Cancelled successfully");
	}
	
	
	
}
