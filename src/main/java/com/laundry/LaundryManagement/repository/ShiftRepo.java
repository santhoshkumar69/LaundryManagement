package com.laundry.LaundryManagement.repository;

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

	@Query("select coalesce(sum(p.paidAmount), 0) from PaymentDetails p where p.shiftId = :shiftId"
			+ " and lower(p.paidMode) = 'cash' and (p.payStatus is null or p.payStatus <> 'RECALLED')")
	Double cashTakenInShift(@Param("shiftId") Long shiftId);

	/** Cash from earlier payments that the cashier handed back (recalled) during the shift. */
	@Query("select coalesce(sum(p.paidAmount), 0) from PaymentDetails p where lower(p.paidMode) = 'cash'"
			+ " and p.payStatus = 'RECALLED' and p.recalledBy = :userName and p.recalledDate >= :from"
			+ " and p.recalledDate <= :to and (p.shiftId is null or p.shiftId <> :shiftId)")
	Double cashRefundedDuringShift(@Param("shiftId") Long shiftId, @Param("userName") String userName,
			@Param("from") Date from, @Param("to") Date to);
}
