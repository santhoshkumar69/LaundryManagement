package com.laundry.LaundryManagement.service;

import com.laundry.LaundryManagement.auth.RequestScope;

import com.laundry.LaundryManagement.common.CommonUtills;
import com.laundry.LaundryManagement.dto.ChangePasswordRequest;
import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.repository.UserInfoRepository;

@Service
public class LoginServiceImpl implements LoginService {

	private static final int MIN_PASSWORD_LENGTH = 6;

	@Autowired
	UserInfoRepository userInfoRepo;
	
	@Override
	@Transactional
	public void changePassword(ChangePasswordRequest request, GlobalInfo globalInfo) {
		if (request == null || request.getUserId() == null || isBlank(request.getCurrentPassword())
				|| isBlank(request.getNewPassword())) {
			throw new IllegalArgumentException("User ID, current password and new password are required");
		}
		if (request.getNewPassword().length() < MIN_PASSWORD_LENGTH) {
			throw new IllegalArgumentException(
					"New password must be at least " + MIN_PASSWORD_LENGTH + " characters");
		}
		if (request.getNewPassword().equals(request.getCurrentPassword())) {
			throw new IllegalArgumentException("New password must be different from the current password");
		}
		RequestScope.Scope scope = RequestScope.require();
		if (!request.getUserId().equals(scope.loginId())) {
			throw new IllegalArgumentException("You can only change your own password");
		}
		UserInfo user = userInfoRepo.findForLogin(scope.tenantId(), request.getUserId(), request.getCurrentPassword());
		if (user == null) {
			throw new IllegalArgumentException("Current password is incorrect");
		}
		user.setPassword(request.getNewPassword());
		CommonUtills.addDefaultUpdateInfos(user, user.getClass(), globalInfo);
		userInfoRepo.save(user);
	}

	private static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
	
}
