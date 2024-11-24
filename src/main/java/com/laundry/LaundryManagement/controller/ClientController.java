package com.laundry.LaundryManagement.controller;



import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.laundry.LaundryManagement.common.CommonUtills;
import com.laundry.LaundryManagement.constant.AppConstants;
import com.laundry.LaundryManagement.constant.AppStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.model.CustomerInfo;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.service.CustomerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("customerInfo")
public class ClientController {

	@Autowired
    CustomerInfoService customerInfoService;

	@PostMapping("/createCustomerInfo")
	public ResponseEntity<Object> createCustomerDetails(@RequestBody CustomerInfo customerInfo, HttpSession httpSession) {
		CommonUtills.addDefaultCreateInfos(customerInfo, customerInfo.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
		
		CustomerInfo acustomerInfo=null;
		if(customerInfo.getQatarId()!= null || !customerInfo.getMobileNo().isEmpty())
		{
			if(customerInfo.getQatarId()!=null)
		{
			Long id=customerInfo.getQatarId();
			acustomerInfo=customerInfoService.findByQatarId(id);	
		}
		
		if(!customerInfo.getMobileNo().isEmpty())
		{
			String mobileNo = customerInfo.getMobileNo();
			acustomerInfo=customerInfoService.findByMobileNo(mobileNo);
		}
		if(acustomerInfo != null)
		{
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_ALREDY_EXIST, AppStatusCode.INTERNAL_ERROR));
		}
		else
		{
			customerInfo.setActiveStatus(1);
		boolean isCreated = customerInfoService.createCustomerInfo(customerInfo);
		if (isCreated) {
			return ResponseEntity.ok().body(customerInfoService.fetchCustomerInfoDetails());
		}}
		}else
		{
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.QatarId_MobileNo_Enter, AppStatusCode.INTERNAL_ERROR));
		}
		return ResponseEntity.status(500)
				.body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE, AppStatusCode.INTERNAL_ERROR));
		}
	
	
	@GetMapping("/fetchclients")
	public ResponseEntity<Object> getClientDetails(HttpSession httpSession) {
			return ResponseEntity.ok().body(customerInfoService.fetchCustomerInfoDetails());
	}
	
	@GetMapping("/deActiveCustomers")
	public ResponseEntity<Object> getDeactiveDetails(HttpSession httpSession)
	{
		return ResponseEntity.ok().body(customerInfoService.deActiveFetchCustomer());
	}

	@PostMapping("/updateCustomerInfo")
	public ResponseEntity<Object> updateCustomerInfoDetails(@RequestBody CustomerInfo customerInfoFromUi, HttpSession httpSession) {
		
		boolean customerInfoIdChanged = false;
		Integer findRow =customerInfoFromUi.getId();
		Optional<CustomerInfo> customerInfoGetFromDb=customerInfoService.findOne(findRow);
		CustomerInfo aCustomerInfo = null;
		
		if(customerInfoGetFromDb==null)
		{
			return ResponseEntity.notFound().build();
		}
		if(customerInfoFromUi.getId() != null && customerInfoGetFromDb.get().getId().equals(customerInfoFromUi.getId()))
		{
			customerInfoIdChanged = true;
		}
		else
		{
			if(customerInfoFromUi.getQatarId() != 0 && !customerInfoGetFromDb.get().getQatarId().equals(customerInfoFromUi.getQatarId()))
			{
				long id= customerInfoFromUi.getQatarId();
				aCustomerInfo=customerInfoService.findByQatarId(id);
			}
			if(customerInfoIdChanged || aCustomerInfo !=null)
			{
				return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_ALREDY_EXIST,AppStatusCode.INTERNAL_ERROR));
			}
		}
		CustomerInfo info=customerInfoGetFromDb.get();
		
		info.setCustomerName(customerInfoFromUi.getCustomerName());
		info.setMobileNo(customerInfoFromUi.getMobileNo());
		info.setQatarId(customerInfoFromUi.getQatarId());
		info.setAddress(customerInfoFromUi.getAddress());
		info.setRemarks(customerInfoFromUi.getRemarks());
		info.setCreatedBy(info.getCreatedBy());
		info.setCreatedDate(info.getCreatedDate());
		
		CommonUtills.addDefaultUpdateInfos(info,info.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
		boolean isCreated = customerInfoService.createCustomerInfo(info);
		if (isCreated) {
			return ResponseEntity.ok().body(customerInfoService.fetchCustomerInfoDetails());
		}

		return ResponseEntity.status(500)
				.body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE, AppStatusCode.INTERNAL_ERROR));

	}

	@PostMapping("/deActiveCustomerInfo")
	public ResponseEntity<Object> deactivateItemDetails(@RequestParam("customerId") Integer customerId,HttpSession httpSession) {
		
		CustomerInfo aCustomerInfo = customerInfoService.findDeleteId(customerId);
		if(aCustomerInfo == null)
		{
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_NOT_EXIST,AppStatusCode.INTERNAL_ERROR));
		}
		if(aCustomerInfo.getActiveStatus() == 0)
		{
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_ALREADY_ACTIVATED,AppStatusCode.INTERNAL_ERROR));
		}
		CommonUtills.addDefaultUpdateInfos(aCustomerInfo, aCustomerInfo.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ) );
		aCustomerInfo.setActiveStatus(0);
		customerInfoService.createCustomerInfo(aCustomerInfo);
		//customerInfoService.deactivateCustomerInfo(customerId);
		return ResponseEntity.ok().body(new ReturnResponse(AppStatus.DEACTIVATED_SUCCESSFULLY, AppStatusCode.OK));
	}

	@PostMapping("/activeCustomer")
	public ResponseEntity<Object> activateItemDetails(@RequestParam("customerId") Integer customerId,HttpSession httpSession) {
		
		CustomerInfo aCustomerInfo = customerInfoService.findDeleteId(customerId);
		if(aCustomerInfo == null)
		{
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_NOT_EXIST,AppStatusCode.INTERNAL_ERROR));
		}
		if(aCustomerInfo.getActiveStatus() ==1 )
		{
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_ALREADY_ACTIVATED,AppStatusCode.INTERNAL_ERROR));
		}
		CommonUtills.addDefaultUpdateInfos(aCustomerInfo, aCustomerInfo.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ) );
		aCustomerInfo.setActiveStatus(1);
		customerInfoService.createCustomerInfo(aCustomerInfo);
		//customerInfoService.deactivateCustomerInfo(customerId);
		return ResponseEntity.ok().body(new ReturnResponse(AppStatus.ACTIVATED_SUCCESSFULLY, AppStatusCode.OK));
	}


}
