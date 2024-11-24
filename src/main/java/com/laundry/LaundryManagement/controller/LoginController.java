package com.laundry.LaundryManagement.controller;

import javax.servlet.http.HttpSession;

import com.laundry.LaundryManagement.common.CommonUtills;
import com.laundry.LaundryManagement.constant.AppConstants;
import com.laundry.LaundryManagement.constant.AppStatusCode;
import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.model.UserInfo;
import com.laundry.LaundryManagement.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("login")
public class LoginController {

	@Autowired
    LoginService loginService;

	@PostMapping(value = "/validateLoginDetails")
	public ResponseEntity<Object> validateLoginDetails(@RequestBody UserInfo userinfo, HttpSession httpSession) {
		if (userinfo == null || userinfo.getUserId() == null || userinfo.getPassword() == null) {
			return ResponseEntity.status(500)
					.body(new ReturnResponse("Enter credentials", AppStatusCode.INTERNAL_ERROR));
		}
		else {
			UserInfo authUserInfo = this.loginService.validateLoginCredentials(userinfo);
			if(authUserInfo == null) {
				return ResponseEntity.status(500)
						.body(new ReturnResponse("Invalid credentials", AppStatusCode.INTERNAL_ERROR));
			}
			httpSession.setAttribute(AppConstants.SESSION_OBJ, CommonUtills.createSessionObject(authUserInfo));
			return ResponseEntity.ok(authUserInfo);
		}
	 
	}

}
