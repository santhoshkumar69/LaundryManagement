package com.laundry.LaundryManagement.controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.laundry.LaundryManagement.common.CommonUtills;
import com.laundry.LaundryManagement.constant.AppConstants;
import com.laundry.LaundryManagement.constant.AppStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.model.AccountType;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.service.AccountTypeService;
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
@RequestMapping("accountType")
public class AccountTypeController {
	
	@Autowired
    AccountTypeService accountTypeService;
	
	@PostMapping("/createAccountType")
	public ResponseEntity<Object> createAccountType(@RequestBody AccountType accountType, HttpSession httpSession)
	{
		CommonUtills.addDefaultCreateInfos(accountType, accountType.getClass(), (GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
         AccountType aAccountType = null;
         
         if(accountType.getAccountType() == null)
         {
        	 return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ACCOUNT_TYPE_ENTER, AppStatusCode.INTERNAL_ERROR));
         }else
         {
        	 accountType.setActiveStatus(1);
        	 boolean isCreated=accountTypeService.createAccountType(accountType);
        	 if(isCreated)
        	 {
        		 return ResponseEntity.ok(accountTypeService.findAllAccountType());
        	 }
        	 return	ResponseEntity.status(500).body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE,AppStatusCode.INTERNAL_ERROR));
         }
				
	}

	@GetMapping("/getAccountType")
	public ResponseEntity<Object>  getAllAccountType(HttpSession httpSession)
	{
		return ResponseEntity.ok().body(accountTypeService.findAllAccountType());
		
	}

	@GetMapping("/getDeAccountType")
	public ResponseEntity<Object> getDeactiveAccountType(HttpSession httpSession)
	{
		return ResponseEntity.ok().body(accountTypeService.findDeActiveAccountType());
	}
	
	@PostMapping("/updateAccountType")
	public ResponseEntity<Object>  updateAccountType(@RequestBody AccountType accountType,HttpSession httpSession)
	{
		Integer id=accountType.getId();
		Optional<AccountType> accountTypeFromDb=accountTypeService.findIdFromDb(id);
		if(accountTypeFromDb == null)
		 {
			  return ResponseEntity.notFound().build();
		  }
		
		AccountType setAccountType = accountTypeFromDb.get();
		
		setAccountType.setAccountType(accountType.getAccountType());
		setAccountType.setUpdatedBy(setAccountType.getUpdatedBy());
		setAccountType.setUpdatedDate(setAccountType.getUpdatedDate());
		
		 CommonUtills.addDefaultUpdateInfos(setAccountType, setAccountType.getClass(), (GlobalInfo) httpSession.getAttribute(AppConstants.SESSION_OBJ));
	     boolean isCreated = accountTypeService.createAccountType(setAccountType);
	     if(isCreated)
	     {
	    	 return ResponseEntity.ok().body(accountTypeService.findAllAccountType());
	     }
	     return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE, AppStatusCode.INTERNAL_ERROR));
	  
		
	}
	
	
	 @PostMapping("/deActiveAccountType")
	  public ResponseEntity<Object> deActiveAccountType(@RequestBody AccountType accountType,HttpSession httpSession)
	  {
		  Integer id = accountType.getId();
		  AccountType aAccountType = accountTypeService.findByRow(id);
		  if(aAccountType == null)
		  {
			  return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_NOT_EXIST, AppStatusCode.INTERNAL_ERROR));
		  }
		  if(aAccountType.getActiveStatus() == 0)
		  {
			  return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_ALREADY_DEACTIVATED, AppStatusCode.INTERNAL_ERROR));
		  }
		  CommonUtills.addDefaultUpdateInfos(aAccountType, aAccountType.getClass(), (GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
		  
		  aAccountType.setActiveStatus(0);
		  accountTypeService.createAccountType(aAccountType);
		  return ResponseEntity.ok().body(new ReturnResponse(AppStatus.DEACTIVATED_SUCCESSFULLY, AppStatusCode.OK));
	  }
	 
}
