package com.laundry.LaundryManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.model.RoleDetails;

@Repository
@Transactional(readOnly = false)
public interface UserRoleRepo extends JpaRepository<RoleDetails, Long> {
	
	@Override
	@Query("From RoleDetails roleDetails where roleDetails.activeStatus=1")
	List<RoleDetails> findAll();
	
	@Modifying
	@Query("update RoleDetails roleDetails set roleDetails.activeStatus=0 WHERE roleDetails.id=:roleId")
	Integer deactivateUserRole(@Param("roleId") Integer roleId);
	
	@Query("from RoleDetails roleDetails where roleDetails.id=id")
	List<RoleDetails> findById();
	
	
	@Query("from RoleDetails roleDetails where roleDetails.id = :id ")
	Optional<RoleDetails> findByUserIds(@Param("id") Integer id);
	
	@Query("from RoleDetails roleDetails where roleDetails.id = :id ")
	RoleDetails findById(@Param("id") Integer id);
	
	@Query("From RoleDetails role where role.activeStatus=0")
	List<RoleDetails> findAllDeactiveRecords();

	@Query("From RoleDetails roleDetail where roleDetail.id=id")
	RoleDetails findByIds(@Param("id") Integer id);

	@Query("select roleDetails from RoleDetails roleDetails where "
			+ "roleDetails.userRole = :userRole ")
	RoleDetails findRoleByName(@Param("userRole") String userRole);
	
	
	


}
