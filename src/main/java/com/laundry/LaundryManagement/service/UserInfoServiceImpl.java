package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.laundry.LaundryManagement.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.repository.UserInfoRepository;


@Service
public class UserInfoServiceImpl implements UserInfoService {
	
	@Autowired
	UserInfoRepository userInfoRepo;

	@Override
	@Transactional
	public boolean createUserInfo(UserInfo userInfo) {
		
		try {
			//userInfo.setActiveStatus(1);
			userInfoRepo.save(userInfo);
			return true;
		}catch (Exception Ex){
       Ex.printStackTrace();
       return false;
		}
	}

	@Override
	@Transactional
	public List<UserInfo> fetchUserInfoDetails() {
		
		return userInfoRepo.findAll();
	}
	
	@Override
	public List<UserInfo> getMobileData() {
		// TODO Auto-generated method stub
		return userInfoRepo.getMobileNoData();
	}

	@Override
	@Transactional
	public void deactivateUserInfo(Integer userId) {
		
		userInfoRepo.deactivateUserInfo(userId);
	}

	@Override
	@Transactional
	public Optional<UserInfo> findOne(Integer id) {
		return userInfoRepo.findById(id);
	}
	
	@Override
	@Transactional
	public UserInfo findByUserId(Long userId) {
		return userInfoRepo.findByUserId(userId);
	}

	@Override
	@Transactional
	public void activateUser(Integer userId) {
		userInfoRepo.activateUserInfo(userId);
	}
//
	@Override
	@Transactional
	public List<UserInfo> fetchAllDeActivateUser() {
	return	userInfoRepo.getAllDeActiveUser();
		
	}

	

	
}
