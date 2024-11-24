package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.UserInfo;

public interface UserInfoService {

	boolean createUserInfo(UserInfo userInfo);

	List<UserInfo> fetchUserInfoDetails();
	
	void deactivateUserInfo(Integer userId);
	
	Optional<UserInfo> findOne(Integer id);

	UserInfo findByUserId(Long userId);
	
	void activateUser(Integer userId);
//	
	List<UserInfo> fetchAllDeActivateUser();
	
	List<UserInfo> getMobileData();
	
	
	
}
