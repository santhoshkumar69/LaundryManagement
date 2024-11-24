package com.laundry.LaundryManagement.repository;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.CustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly=false)
public interface CustomerInfoRepo extends JpaRepository<CustomerInfo, Integer> {

	@Override
	@Query("From CustomerInfo customerInfo where customerInfo.activeStatus=1")
	List<CustomerInfo> findAll();
	
	@Modifying
	@Query("update CustomerInfo customerInfo set customerInfo.activeStatus=0 WHERE customerInfo.id=:customerId")
	Integer deactivateCustomerInfo(@Param("customerId") Integer customerId);
	
	
	@Query("from CustomerInfo customerInfo where customerInfo.id=id")
	List<CustomerInfo> findByIds();

	@Query("select customerInfo from CustomerInfo customerInfo where "
			+ "customerInfo.qatarId = :qatarId ")
	CustomerInfo findByQatarId(@Param("qatarId") Long qatarId);
	
	@Query("select customerInfo from CustomerInfo customerInfo where "+"customerInfo.MobileNo = :MobileNo")
	CustomerInfo findByMobNo(@Param("MobileNo") String MobileNo);
	
	@Query("from CustomerInfo customerInfo where customerInfo.id = :id ")
	CustomerInfo findByIdDelete(@Param("id") Integer id);
	
	@Query("From CustomerInfo customerInfo where customerInfo.activeStatus=0")
	List<CustomerInfo> deactiveRecords();

	Optional<CustomerInfo> findById(Integer id);
	
}
