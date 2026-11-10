package com.laundry.LaundryManagement.service;

import com.laundry.LaundryManagement.dto.ChangePasswordRequest;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.model.UserInfo;

public interface LoginService {
	

	void changePassword(ChangePasswordRequest request, GlobalInfo globalInfo);

}
