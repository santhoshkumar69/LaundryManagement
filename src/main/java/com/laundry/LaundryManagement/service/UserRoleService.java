package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.RoleDetails;

public interface UserRoleService {
	  
	boolean createUserRole(RoleDetails roleDetails);
	
	List<RoleDetails> fetchRoleDetails();
	
	void deactiveUserRoles(Integer roleId);
	
	Optional<RoleDetails> findOne(Integer id);

	List deActiveRoleDetails();
	
	RoleDetails findByID(Integer id);
	
	RoleDetails findByName(String role);
}
