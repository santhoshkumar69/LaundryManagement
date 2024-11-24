package com.laundry.LaundryManagement.service;

import com.laundry.LaundryManagement.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.repository.UserInfoRepository;

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	UserInfoRepository userInfoRepo;
	
	public UserInfo validateLoginCredentials(UserInfo userinfo) {
		UserInfo userInfo = this.userInfoRepo.findByLaundryUserId(userinfo.getUserId(), userinfo.getPassword());
		return userInfo;
	}
	
}
