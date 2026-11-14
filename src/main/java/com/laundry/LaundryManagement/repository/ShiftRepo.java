package com.laundry.LaundryManagement.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.laundry.LaundryManagement.model.Shift;

@Repository
public interface ShiftRepo extends JpaRepository<Shift, Long> {

	/** The user's open shift; run as system to find it in whichever outlet it was started. */
	@Query("from Shift s where s.tenantId = :tenantId and s.userDbId = :userDbId and s.status = 'OPEN'")
	List<Shift> findOpen(@Param("tenantId") Integer tenantId, @Param("userDbId") Integer userDbId);

	/** Current outlet's shifts started in the period, plus any still open; userDbId null means all cashiers. */
	@Query("from Shift s where (s.status = 'OPEN' or (s.startTime >= :from and s.startTime < :to))"
			+ " and (:userDbId is null or s.userDbId = :userDbId) order by s.startTime desc")
	List<Shift> findForReport(@Param("from") Date from, @Param("to") Date to, @Param("userDbId") Integer userDbId);

	// A payment or expense counts as cash when its mode is named "Cash" (any case, surrounding spaces ignored).

	/** Cash taken at billing during the shift, including payments recalled later (recalls are subtracted separately). */
	@Query("select coalesce(sum(p.paidAmount), 0) from PaymentDetails p where p.shiftId = :shiftId"
			+ " and lower(trim(p.paidMode)) = 'cash' and (p.paySource is null or p.paySource <> 'DELIVERY')")
	Double billCash(@Param("shiftId") Long shiftId);

	/** Cash taken on the Delivery screen during the shift (each part of a split payment is its own row). */
	@Query("select coalesce(sum(p.paidAmount), 0) from PaymentDetails p where p.shiftId = :shiftId"
			+ " and lower(trim(p.paidMode)) = 'cash' and p.paySource = 'DELIVERY'")
	Double deliveryCash(@Param("shiftId") Long shiftId);

	/** Cash payments recalled during the shift, whenever they were taken. */
	@Query("select coalesce(sum(p.paidAmount), 0) from PaymentDetails p where p.recallShiftId = :shiftId"
			+ " and lower(trim(p.paidMode)) = 'cash' and p.payStatus = 'RECALLED'")
	Double recalledCash(@Param("shiftId") Long shiftId);

	/** Live expenses paid in cash that were recorded during the shift. */
	@Query("select coalesce(sum(e.amount), 0) from Expense e where e.shiftId = :shiftId and e.activeStatus = 1"
			+ " and lower(trim(e.paymentMode)) = 'cash'")
	BigDecimal expenseCash(@Param("shiftId") Long shiftId);

	@Query("select s.status from Shift s where s.id = :shiftId")
	String statusOf(@Param("shiftId") Long shiftId);
}
