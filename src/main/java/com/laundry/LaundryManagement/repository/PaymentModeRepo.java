package com.laundry.LaundryManagement.repository;

import java.util.List;

import com.laundry.LaundryManagement.model.PaymentMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = false)
public interface PaymentModeRepo extends JpaRepository<PaymentMode, Long>  {

	PaymentMode findByPaymentMode(String paymentMode);
	
	@Query("From PaymentMode paymentMode where paymentMode.activeStatus = 0")
	List<PaymentMode> findInactivePaymentMode();

}
