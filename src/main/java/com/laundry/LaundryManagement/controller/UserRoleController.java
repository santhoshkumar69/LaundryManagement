package com.laundry.LaundryManagement.controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.laundry.LaundryManagement.common.CommonUtills;
import com.laundry.LaundryManagement.constant.AppConstants;
import com.laundry.LaundryManagement.constant.AppStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.model.RoleDetails;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("userRole")
public class UserRoleController {
	
	@Autowired
    UserRoleService userRoleService;

	@PostMapping("/createUserRole")
	public ResponseEntity<Object> createUserRole(@RequestBody RoleDetails roleDetails,HttpSession httpSession){
		CommonUtills.addDefaultCreateInfos(roleDetails,roleDetails.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
		
		RoleDetails aRoleDetails = null;
		if(roleDetails.getUserRole() != null)
		{
			String name = roleDetails.getUserRole();
			aRoleDetails=userRoleService.findByName(name);
		}
		if (aRoleDetails != null) {
			return ResponseEntity.status(500)
					.body(new ReturnResponse(AppStatus.ID_ALREDY_EXIST, AppStatusCode.INTERNAL_ERROR));
		} else {
			roleDetails.setActiveStatus(1);
			boolean isCreated = userRoleService.createUserRole(roleDetails);
			
				if(isCreated){
			return ResponseEntity.ok().body(userRoleService.fetchRoleDetails());
		}
		return	ResponseEntity.status(500).body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE,AppStatusCode.INTERNAL_ERROR));
	}}
	
	@PostMapping("/updateUserRole")
	public ResponseEntity<Object> updateUserRoleDetails(@RequestBody RoleDetails roleDetailsFromUi, HttpSession httpSession) {
		boolean roleIdChanged=false;
		Integer id= roleDetailsFromUi.getId();
	     Optional<RoleDetails> roleetailsFromDb=userRoleService.findOne(id);
	     RoleDetails  roleDetails = null;
	     if(roleetailsFromDb==null)
			{
				return ResponseEntity.notFound().build();
			}
	     if(roleDetailsFromUi.getId() != null && !roleetailsFromDb.get().getId().equals(roleDetailsFromUi.getId()))
	     {
	    	 roleIdChanged = true;
	     }
	     else {
	    	 if(roleDetailsFromUi.getUserRole() != null && !roleetailsFromDb.get().getUserRole().equals(roleDetailsFromUi.getUserRole()))
	    	 {
	    		String roleName = roleDetailsFromUi.getUserRole();
	    		roleDetails =userRoleService.findByName(roleName);
	    	 }
	    	 if(roleIdChanged || roleDetails != null)
	    	 {
	    		 return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.User_Role_ALREDY_EXIST,AppStatusCode.INTERNAL_ERROR)); 
	    	 }
	     }
		RoleDetails setRole=roleetailsFromDb.get();
		setRole.setUserRole(roleDetailsFromUi.getUserRole());
		setRole.setCreatedBy(setRole.getCreatedBy());
		setRole.setCreatedDate(setRole.getCreatedDate());
	     
		CommonUtills.addDefaultUpdateInfos(setRole, setRole.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
		boolean isCreated = userRoleService.createUserRole(setRole);
		if (isCreated) {
			return ResponseEntity.ok().body(userRoleService.fetchRoleDetails());
		}

		return ResponseEntity.status(500)
				.body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE, AppStatusCode.INTERNAL_ERROR));

	}

	@PostMapping("/deActiveUserRole")
	public ResponseEntity<Object> deactivateUserRole(@RequestParam("roleId") Integer roleId,HttpSession httpSession) {
		
		RoleDetails aRoleDetails = userRoleService.findByID(roleId);
		if(aRoleDetails == null)
		{
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_NOT_EXIST,AppStatusCode.INTERNAL_ERROR));	
		}
		if(aRoleDetails.getActiveStatus() == 0)
		{
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_ALREADY_DEACTIVATED,AppStatusCode.INTERNAL_ERROR));
		}
		CommonUtills.addDefaultUpdateInfos(aRoleDetails, aRoleDetails.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ) );
		aRoleDetails.setActiveStatus(0);
		userRoleService.createUserRole(aRoleDetails);
		
		//userRoleService.deactiveUserRoles(roleId);
		return ResponseEntity.ok().body(new ReturnResponse(AppStatus.DEACTIVATED_SUCCESSFULLY, AppStatusCode.OK));
	}
	
	@PostMapping("/activeUserRole")
	public ResponseEntity<Object> activateUserRoleDetails(@RequestParam("roleId") Integer roleId,HttpSession httpSession) {
		
		RoleDetails aRoleDetails = userRoleService.findByID(roleId);
		if(aRoleDetails == null)
		{
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_NOT_EXIST,AppStatusCode.INTERNAL_ERROR));	
		}
		if(aRoleDetails.getActiveStatus() == 1)
		{
			return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_ALREADY_ACTIVATED,AppStatusCode.INTERNAL_ERROR));
		}
		CommonUtills.addDefaultUpdateInfos(aRoleDetails, aRoleDetails.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ) );
		aRoleDetails.setActiveStatus(1);
		userRoleService.createUserRole(aRoleDetails);
		
		//userRoleService.deactiveUserRoles(roleId);
		return ResponseEntity.ok().body(new ReturnResponse(AppStatus.ACTIVATED_SUCCESSFULLY, AppStatusCode.OK));
	}
	
	@GetMapping("/fetchUserRole")
	public ResponseEntity<Object> getAllRoleDetails(HttpSession httpSession) {
			return ResponseEntity.ok().body(userRoleService.fetchRoleDetails());
	}
	
	@GetMapping("/fetchDeactiveUserRole")
	public ResponseEntity<Object> getAllDeactiveDetaials()
	{
		return ResponseEntity.ok().body(userRoleService.deActiveRoleDetails());
		
	}
}
