package com.laundry.LaundryManagement.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.laundry.LaundryManagement.model.WashingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface WashingTypeRepo extends JpaRepository<WashingType, Integer> {

	@Query("From WashingType washingType where washingType.activeStatus=1")
	List<WashingType> findAllWashingType();
	
	@Query("From WashingType deWashingType where deWashingType.activeStatus=0")
	List<WashingType> findDeActiveWashingType();
	
	@Query("select washingType from WashingType washingType WHERE " + "washingType.processTypeId=:processTypeId")
	WashingType findByIds(@Param("processTypeId") Integer processTypeId);
	
	@Query("from WashingType washingType where washingType.processTypeId=:processTypeId")
	Optional<WashingType> findByIdRow(@Param("processTypeId") Integer processTypeId);
	
	@Query("from WashingType washingTypeId where washingTypeId.processTypeId = :processTypeId ")
	WashingType findByIdss(@Param("processTypeId") Integer processTypeId);
	
	@Modifying
	@Query("update WashingType deactive set deactive.activeStatus=0 WHERE deactive.processTypeId=:processTypeId")
	Integer deActiveWashingType(@Param("processTypeId") Integer processTypeId);
	
	@Query("select washingType from WashingType washingType where "
			+ "washingType.processTypeName = :processTypeName ")
	WashingType findProcessTypeName(@Param("processTypeName") String processTypeName);
}
