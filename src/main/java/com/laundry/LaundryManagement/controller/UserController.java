package com.laundry.LaundryManagement.controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.laundry.LaundryManagement.common.CommonUtills;
import com.laundry.LaundryManagement.constant.AppConstants;
import com.laundry.LaundryManagement.constant.AppStatus;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.model.UserInfo;
import com.laundry.LaundryManagement.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "http://localhost:4200",maxAge = 3600)
@RestController
@RequestMapping("userInfo")
public class UserController {
	
	 @Autowired
     UserInfoService userInfoService;
	    
	    
		@PostMapping("/createUserInfo")
		public ResponseEntity<Object> createUserInfoDetails(@RequestBody UserInfo userInfo, HttpSession httpSession){
			CommonUtills.addDefaultCreateInfos(userInfo,userInfo.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
	        UserInfo aUserInfo = null;
			if(userInfo.getUserId() != null) {
				Long userId =  userInfo.getUserId();
				aUserInfo = userInfoService.findByUserId(userId); 
			}
			if(aUserInfo != null) {
				return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_ALREDY_EXIST, AppStatusCode.INTERNAL_ERROR));
			}
			else {
			userInfo.setActiveStatus(1);
			boolean isCreated = userInfoService.createUserInfo(userInfo);
			if(isCreated){
				return ResponseEntity.ok().body(userInfoService.fetchUserInfoDetails());
			}
			return	ResponseEntity.status(500).body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE,AppStatusCode.INTERNAL_ERROR));
			}
			
		}

		@GetMapping("/getUserInfoDetails")
		public ResponseEntity<Object> getUserInfoDetails(HttpSession httpSession)
		{
			return ResponseEntity.ok().body(userInfoService.fetchUserInfoDetails());
			
			
		}
		
		@GetMapping("/getmobile")
		public ResponseEntity<Object> getMobile(HttpSession httpSession)
		{
			return ResponseEntity.ok().body(userInfoService.getMobileData());
			
			
		}
		
		@PostMapping("/updateUserinfo")
		public ResponseEntity<Object> updateUserInfoDetails(@RequestBody UserInfo userInfo, HttpSession httpSession) {
			boolean userIdChanged = false;
			Integer id = userInfo.getId();
			Optional<UserInfo> userInfoFromDB = userInfoService.findOne(id);
			if (userInfoFromDB == null) {
				return ResponseEntity.notFound().build();
			}
			if(userInfo.getUserId() != null && !userInfoFromDB.get().getUserId().equals(userInfo.getUserId())) {
				Long newUserId = userInfo.getUserId();
				UserInfo aUserInfo = userInfoService.findByUserId(newUserId); 
				if(aUserInfo != null) {
					userIdChanged = true;
				}
			}
			if(userIdChanged) {
				return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_ALREDY_EXIST,AppStatusCode.INTERNAL_ERROR));
			}
			UserInfo setrow = userInfoFromDB.get();
			setrow.setUserId(userInfo.getUserId());
			setrow.setUserName(userInfo.getUserName());
			setrow.setAddress(userInfo.getAddress());
			setrow.setRole(userInfo.getRole());
			setrow.setGender(userInfo.getGender());
			setrow.setBloodGroup(userInfo.getBloodGroup());
			setrow.setPassword(userInfo.getPassword());
			setrow.setMobileNumber(userInfo.getMobileNumber());
			setrow.setRemarks(userInfo.getRemarks());
			setrow.setCreatedBy(setrow.getCreatedBy());
			setrow.setCreatedDate(setrow.getCreatedDate());
		

			CommonUtills.addDefaultUpdateInfos(setrow, setrow.getClass(),(GlobalInfo) httpSession.getAttribute(AppConstants.SESSION_OBJ));
			boolean isCreated = userInfoService.createUserInfo(setrow);
			if (isCreated) {
				return ResponseEntity.ok().body(userInfoService.fetchUserInfoDetails());
			}
			return ResponseEntity.status(500)
					.body(new ReturnResponse(AppStatus.UNABLE_TO_CREATE, AppStatusCode.INTERNAL_ERROR));

		}
		
		@PostMapping("/deActiveUserInfo")
		public ResponseEntity<Object> deactiveUserInfoDetails(@RequestParam("userId") Integer userId, HttpSession httpSession) {
			UserInfo aUserInfo = userInfoService.findByUserId(userId.longValue()); 
			if(aUserInfo == null) {
				return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_NOT_EXIST,AppStatusCode.INTERNAL_ERROR));
			}
			if(aUserInfo.getActiveStatus() == 0) {
				return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_ALREADY_DEACTIVATED,AppStatusCode.INTERNAL_ERROR));
			}
			CommonUtills.addDefaultUpdateInfos(aUserInfo,aUserInfo.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
			aUserInfo.setActiveStatus(0);
			userInfoService.createUserInfo(aUserInfo);
			//userInfoService.deactivateUserInfo(userId);
			return ResponseEntity.ok().body(new ReturnResponse(AppStatus.DEACTIVATED_SUCCESSFULLY, AppStatusCode.OK));
		}

		@PostMapping("/activateUserInfo")
		public ResponseEntity<Object> activeStatausUserInfo(@RequestParam("userId") Integer userId,HttpSession httpSession)
		{
			UserInfo aUserInfo =userInfoService.findByUserId(userId.longValue()); 
			if(aUserInfo == null) {
				return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_NOT_EXIST,AppStatusCode.INTERNAL_ERROR));
			}
			if(aUserInfo.getActiveStatus() == 1) {
				return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_ALREADY_DEACTIVATED,AppStatusCode.INTERNAL_ERROR));
				
			}		
			CommonUtills.addDefaultUpdateInfos(aUserInfo,aUserInfo.getClass(),(GlobalInfo)httpSession.getAttribute(AppConstants.SESSION_OBJ));
			aUserInfo.setActiveStatus(1);
			userInfoService.createUserInfo(aUserInfo);
			return ResponseEntity.ok().body(new ReturnResponse(AppStatus.ACTIVATED_SUCCESSFULLY, AppStatusCode.OK));
			
		}
		
		@GetMapping("/getDeactiveUserInfoDetails")
		public ResponseEntity<Object> getDeActiveUserInfoDetails(HttpSession httpSession)
		{
			return ResponseEntity.ok().body(userInfoService.fetchAllDeActivateUser());
			
			
		}
		
		@PostMapping("/getParticularRow")
		public ResponseEntity<Object> getParticularRow(@RequestParam("userId") Long userId,HttpSession httpSession)
		{
			UserInfo aUserInfo =userInfoService.findByUserId(userId.longValue()); 
			if(aUserInfo == null) {
				return ResponseEntity.status(500).body(new ReturnResponse(AppStatus.ID_NOT_EXIST,AppStatusCode.INTERNAL_ERROR));
			}
			
			return ResponseEntity.ok(aUserInfo);
		}

}
