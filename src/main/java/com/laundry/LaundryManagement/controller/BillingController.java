package com.laundry.LaundryManagement.controller;

import com.laundry.LaundryManagement.auth.RequestScope;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.laundry.LaundryManagement.constant.AppConstants;
import com.laundry.LaundryManagement.model.CustomerInfo;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.service.BillingService;
import com.laundry.LaundryManagement.service.MandatoryFields;
import com.laundry.LaundryManagement.service.MasterService;
import com.laundry.LaundryManagement.service.OutletMandatoryFields;
import com.laundry.LaundryManagement.service.ShiftService;
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
import com.laundry.LaundryManagement.dto.DashboardDto;
import com.laundry.LaundryManagement.dto.RecallPaymentRequest;
import com.laundry.LaundryManagement.dto.UpdateItemRemarksRequest;
import com.laundry.LaundryManagement.dto.ErrorResponse;
import com.laundry.LaundryManagement.dto.OrderStatusDto;
import com.laundry.LaundryManagement.dto.PayOrderRequest;
import com.laundry.LaundryManagement.dto.RemoveOrderItemsRequest;
import com.laundry.LaundryManagement.dto.ReceiptDetailDto;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.dto.UpdateOrderStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
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
	private OutletMandatoryFields outletMandatoryFields;

	@Autowired
	private ShiftService shiftService;
	
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
		shiftService.requireShiftForCashWork();
		BilledDetails billedCompos = billingService.createEstimationDetails(billingReq,RequestScope.globalInfo());
		return ResponseEntity.ok().body(billedCompos);
	}
	
	@PostMapping("/confirmBilling")
	public ResponseEntity<?> confirmBillingDetails(@RequestBody ConfirmBilling confirmBill, HttpSession httpSession) {
		shiftService.requireShiftForCashWork();
		List<String> missing = outletMandatoryFields.missing(Map.of(
				MandatoryFields.DELIVERY_DATE, OutletMandatoryFields.value(confirmBill.getDeliveryDate()),
				MandatoryFields.DELIVERY_ADDRESS, OutletMandatoryFields.value(confirmBill.getDeliveryAddress()),
				MandatoryFields.PICKUP_TYPE, OutletMandatoryFields.value(confirmBill.getPickupType())));
		if (!missing.isEmpty()) {
			return ResponseEntity.badRequest().body(new ReturnResponse(MandatoryFields.message(missing), 400));
		}
		ReceiptDetailDto receiptInfo = billingService.confirmBillingRequest(confirmBill, RequestScope.globalInfo());
		return ResponseEntity.ok().body(receiptInfo);
	}
	
	@PostMapping("/cancelBilling")
	public ResponseEntity<?> cancelBillingReqest(@RequestBody ConfirmBilling confirmBill, HttpSession httpSession) {
		billingService.cancelBillingRequest(confirmBill, RequestScope.globalInfo());
		return ResponseEntity.ok().body("Cancelled successfully");
	}
	
	@GetMapping("/orderStatus")
	public ResponseEntity<List<OrderStatusDto>> getOrderStatus(
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "dateField", required = false) String dateField,
			@RequestParam(value = "includeOverdue", defaultValue = "false") boolean includeOverdue) {
		return ResponseEntity.ok()
				.body(billingService.fetchOrders(fromDate, toDate, search, status, dateField, includeOverdue));
	}

	@GetMapping("/dashboard")
	public ResponseEntity<DashboardDto> getDashboard() {
		return ResponseEntity.ok().body(billingService.fetchDashboard());
	}

	@PostMapping("/updateOrderStatus")
	public ResponseEntity<?> updateOrderStatus(@RequestBody UpdateOrderStatus request, HttpSession httpSession) {
		try {
			return ResponseEntity.ok().body(billingService.updateOrderStatus(request,
					RequestScope.globalInfo()));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(500)
					.body(new ReturnResponse(ex.getMessage(), AppStatusCode.INTERNAL_ERROR));
		}
	}

	@GetMapping("/orderDetail")
	public ResponseEntity<?> getOrderDetail(@RequestParam("invID") int invID) {
		try {
			return ResponseEntity.ok().body(billingService.fetchOrderDetail(invID));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(500)
					.body(new ReturnResponse(ex.getMessage(), AppStatusCode.INTERNAL_ERROR));
		}
	}

	@PostMapping("/payOrder")
	public ResponseEntity<?> payOrder(@RequestBody PayOrderRequest request, HttpSession httpSession) {
		shiftService.requireShiftForCashWork();
		try {
			return ResponseEntity.ok().body(billingService.payOrder(request,
					RequestScope.globalInfo()));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(500)
					.body(new ReturnResponse(ex.getMessage(), AppStatusCode.INTERNAL_ERROR));
		}
	}

	@PostMapping("/removeOrderItems")
	public ResponseEntity<?> removeOrderItems(@RequestBody RemoveOrderItemsRequest request,
			HttpSession httpSession) {
		try {
			return ResponseEntity.ok().body(billingService.removeOrderItems(request,
					RequestScope.globalInfo()));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(500)
					.body(new ReturnResponse(ex.getMessage(), AppStatusCode.INTERNAL_ERROR));
		}
	}

	@PostMapping("/recallPayment")
	public ResponseEntity<?> recallPayment(@RequestBody RecallPaymentRequest request, HttpSession httpSession) {
		shiftService.requireShiftForCashWork();
		try {
			return ResponseEntity.ok().body(billingService.recallPayment(request,
					RequestScope.globalInfo()));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(500)
					.body(new ReturnResponse(ex.getMessage(), AppStatusCode.INTERNAL_ERROR));
		}
	}

	@PostMapping("/updateItemRemarks")
	public ResponseEntity<?> updateItemRemarks(@RequestBody UpdateItemRemarksRequest request, HttpSession httpSession) {
		try {
			return ResponseEntity.ok().body(billingService.updateItemRemarks(request,
					RequestScope.globalInfo()));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(500)
					.body(new ReturnResponse(ex.getMessage(), AppStatusCode.INTERNAL_ERROR));
		}
	}

	@PostMapping("/deleteOrders")
	public ResponseEntity<?> deleteOrders(@RequestBody List<Integer> invIDs, HttpSession httpSession) {
		try {
			int deleted = billingService.deleteOrders(invIDs,
					RequestScope.globalInfo());
			return ResponseEntity.ok().body(new ReturnResponse(deleted + " order(s) deleted", AppStatusCode.OK));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(500)
					.body(new ReturnResponse(ex.getMessage(), AppStatusCode.INTERNAL_ERROR));
		}
	}

	@PostMapping("/removeItem")
	public ResponseEntity<?> removeBillItem(@RequestParam("itemID") int itemID) {
		billingService.removeBillItems(itemID);
		return ResponseEntity.ok().body("Cancelled successfully");
	}
	
	
	
}
