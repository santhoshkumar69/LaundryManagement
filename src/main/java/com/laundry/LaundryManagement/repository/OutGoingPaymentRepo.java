package com.laundry.LaundryManagement.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.laundry.LaundryManagement.model.OutGoingPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface OutGoingPaymentRepo extends JpaRepository<OutGoingPayment, Long>{
	
	@Query("From OutGoingPayment outGoingPayement1 where outGoingPayement1.activeStatus=1")
	List<OutGoingPayment> findAllOutGoingpayment();
	
	@Query("From OutGoingPayment outGoingPayment where outGoingPayment.activeStatus=0")
	List<OutGoingPayment> findDeactiveRecords();
	
	@Modifying
	@Query("update OutGoingPayment outgoing set outgoing.activeStatus=0 WHERE outgoing.id=:id")
	Integer deActiveOutGoingPayment(@Param("id") Integer id);
		
	@Query("select payment from OutGoingPayment payment WHERE " + "payment.id=:id")
	OutGoingPayment findByids(@Param("id") Integer id);
	
	@Query("from OutGoingPayment outGoingPayment where outGoingPayment.id=:id")
	Optional<OutGoingPayment> findByIdRow(@Param("id") Integer id);

}
