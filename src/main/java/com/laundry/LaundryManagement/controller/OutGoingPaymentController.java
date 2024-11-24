package com.laundry.LaundryManagement.controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.laundry.LaundryManagement.common.CommonUtills;
import com.laundry.LaundryManagement.constant.AppConstants;
import com.laundry.LaundryManagement.constant.AppStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.model.OutGoingPayment;
import com.laundry.LaundryManagement.service.OutGoingPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200",maxAge = 3600)
@RestController
@RequestMapping("outGoingPayment")
public class OutGoingPaymentController {
	
	@Autowired
    OutGoingPaymentService outGoingPaymentService;
	
	@PostMapping("/createOutGoingPayment")
	public ResponseEntity<Object> createOutGoingPayment(@RequestBody OutGoingPayment outGoingPayment, HttpSession httpSession)
	{
		
		CommonUtills.addDefaultCreateInfos(outGoingPayment, outGoingPayment.getClass(), (GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
	    OutGoingPayment aOutGoingPayment = null;
	    if(outGoingPayment.getAccountType() == null)
	    {
	    	return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ACCOUNT_TYPE_ENTER, AppStatusCode.INTERNAL_ERROR));
	    }
	    else {
	    	outGoingPayment.setActiveStatus(1);
	    	boolean isCreated = outGoingPaymentService.createOutGoingPayment(outGoingPayment);
	    	if(isCreated)
	    	{
	    		return ResponseEntity.ok().body(outGoingPaymentService.fetchOutGoingPayment());
	    	}
	    	return	ResponseEntity.status(500).body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE,AppStatusCode.INTERNAL_ERROR));
	    }
			
	}
	
	@GetMapping("/getDeActiveOuGoingPayment")
	public ResponseEntity<Object> getDeActiveOutGoingPaymentDetails(HttpSession htpSession)
	{
		return ResponseEntity.ok().body(outGoingPaymentService.fetchOutGoingPayment());
	}
	
  @GetMapping("/getOutGoingPayment")
  public ResponseEntity<Object> getOutGoingPaymentDetails(HttpSession httpSession)
  {
	 return ResponseEntity.ok().body(outGoingPaymentService.fetchOutGoingPayment()); 
  }
  
  @PostMapping("/updateOutGoingPayment")
  public ResponseEntity<Object> updateOutGoingPayment(@RequestBody OutGoingPayment outGoingPayment,HttpSession httpSession)
  {
	  boolean outGoingIdChanged = false;
	  Integer id = outGoingPayment.getId();
	  Optional<OutGoingPayment> outGoingPaymentFromDB = outGoingPaymentService.findOne(id);
	  if(outGoingPaymentFromDB == null)
	  {
		  return ResponseEntity.notFound().build();
	  }
	  
	  OutGoingPayment setOutGoingPayment = outGoingPaymentFromDB.get();
	  setOutGoingPayment.setEntryDate(outGoingPayment.getEntryDate());
	  setOutGoingPayment.setAccountType(outGoingPayment.getAccountType());
	  setOutGoingPayment.setAmount(outGoingPayment.getAmount());
	  setOutGoingPayment.setRemarks(outGoingPayment.getRemarks());
	  setOutGoingPayment.setPaymentType(outGoingPayment.getPaymentType());
	  setOutGoingPayment.setCreatedBy(setOutGoingPayment.getCreatedBy());
	  setOutGoingPayment.setCreatedDate(setOutGoingPayment.getCreatedDate());
	  
	  
	  CommonUtills.addDefaultUpdateInfos(setOutGoingPayment, outGoingPayment.getClass(), (GlobalInfo) httpSession.getAttribute(AppConstants.SESSION_OBJ));
     boolean isCreated = outGoingPaymentService.createOutGoingPayment(setOutGoingPayment);
     if(isCreated)
     {
    	 return ResponseEntity.ok().body(outGoingPaymentService.fetchOutGoingPayment());
     }
     return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE, AppStatusCode.INTERNAL_ERROR));
  
  }
  
  @PostMapping("/deActiveOutGoingPayment")
  public ResponseEntity<Object> deActiveOutGoingPayment(@RequestBody OutGoingPayment outgoingPayment,HttpSession httpSession)
  {
	  Integer id = outgoingPayment.getId();
	  OutGoingPayment aoutgoingPayment = outGoingPaymentService.findById(id);
	  if(aoutgoingPayment == null)
	  {
		  return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_NOT_EXIST, AppStatusCode.INTERNAL_ERROR));
	  }
	  if(aoutgoingPayment.getActiveStatus() == 0)
	  {
		  return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_ALREADY_DEACTIVATED, AppStatusCode.INTERNAL_ERROR));
	  }
	  if(outgoingPayment.getVoidReason() == null)
	  {
		return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.VOID_REASON, AppStatusCode.INTERNAL_ERROR));  
	  }
	  CommonUtills.addDefaultUpdateInfos(aoutgoingPayment, aoutgoingPayment.getClass(), (GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
	  aoutgoingPayment.setVoidReason(outgoingPayment.getVoidReason());
	  aoutgoingPayment.setActiveStatus(0);
	  outGoingPaymentService.createOutGoingPayment(aoutgoingPayment);
	  return ResponseEntity.ok().body(new ReturnResponse(AppStatus.DEACTIVATED_SUCCESSFULLY, AppStatusCode.OK));
  }
 
  
  
}
