package com.laundry.LaundryManagement.repository;

import java.util.List;

import com.laundry.LaundryManagement.model.PaymentMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = false)
public interface PaymentModeRepo extends JpaRepository<PaymentMode, Long>  {

	PaymentMode findByPaymentMode(String paymentMode);
	
	@Query("From PaymentMode paymentMode where paymentMode.activeStatus = 0")
	List<PaymentMode> findInactivePaymentMode();

	@Query("From PaymentMode paymentMode where paymentMode.activeStatus = 1 order by paymentMode.id")
	List<PaymentMode> findActivePaymentMode();

	/** Clears the default flag on the outlet's other modes (the outlet filter applies to bulk updates). */
	@Modifying
	@Query("update PaymentMode p set p.defaultMode = false where p.id <> :keepId")
	int clearOtherDefaults(@Param("keepId") Long keepId);

}
