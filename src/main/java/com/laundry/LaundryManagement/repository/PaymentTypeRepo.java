package com.laundry.LaundryManagement.repository;

import java.util.List;

import com.laundry.LaundryManagement.model.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = false)
public interface PaymentTypeRepo extends JpaRepository<PaymentType, Long>   {

	PaymentType findByPaymentType(String paymentType);
	
	@Query("From PaymentType paymentType where paymentType.activeStatus = 0")
	List<PaymentType> findInactivePaymentType();

}
