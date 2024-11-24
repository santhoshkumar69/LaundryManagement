package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.model.RoleDetails;
import com.laundry.LaundryManagement.repository.UserRoleRepo;


@Service
public class UserRoleServiceImpl implements UserRoleService {

	@Autowired
	UserRoleRepo userRoleService;
	
	@Override
	@Transactional
	public boolean createUserRole(RoleDetails roleDetails) {
		 try {
			// roleDetails.setActiveStatus(1);
			 userRoleService.save(roleDetails);
			 return true;
		 }catch (Exception Ex)
		 {
		 Ex.printStackTrace();
		 return false;
		 }
		
	}

	@Override
	@Transactional
	public List<RoleDetails> fetchRoleDetails() {
	return userRoleService.findAll();
		
	}

	@Override
	@Transactional
	public void deactiveUserRoles(Integer roleId) {
		userRoleService.deactivateUserRole(roleId);
		
	}

	@Override
	@Transactional
	public Optional<RoleDetails> findOne(Integer id) {
		
		return userRoleService.findByUserIds(id);
	}

	@Override
	@Transactional
	public List<RoleDetails> deActiveRoleDetails() {
	
		return userRoleService.findAllDeactiveRecords();
				
	}

	@Override
	@Transactional
	public RoleDetails findByID(Integer id) {
		
		return userRoleService.findById(id);
	}

	@Override
	public RoleDetails findByName(String role) {
		
		return userRoleService.findRoleByName(role);
	}

}
