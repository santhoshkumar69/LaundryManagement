package com.laundry.LaundryManagement.service;

import com.laundry.LaundryManagement.model.UserInfo;

public interface LoginService {
	
	UserInfo validateLoginCredentials(UserInfo userinfo);

}
