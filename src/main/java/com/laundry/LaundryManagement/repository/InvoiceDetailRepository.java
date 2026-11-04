/**
 * 
 */
package com.laundry.LaundryManagement.repository;

import java.util.Date;
import java.util.List;

import com.laundry.LaundryManagement.model.InvoiceDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author pandyarajan
 *
 * 12-Mar-2019
 */
@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetails, Integer>{

	@Query("From InvoiceDetails inv where inv.invoicedDate >= :fromDate and inv.invoicedDate < :toDate"
			+ " and (inv.activeStatus is null or inv.activeStatus <> 0) order by inv.invID desc")
	List<InvoiceDetails> findByInvoicedDateRange(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query("From InvoiceDetails inv where inv.deliveryDate >= :fromDate and inv.deliveryDate < :toDate"
			+ " and (inv.activeStatus is null or inv.activeStatus <> 0) order by inv.deliveryDate, inv.invID")
	List<InvoiceDetails> findByDeliveryDateRange(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query("From InvoiceDetails inv where inv.deliveryDate < :before"
			+ " and (inv.orderStatus is null or inv.orderStatus <> 'COLLECTED')"
			+ " and (inv.activeStatus is null or inv.activeStatus <> 0) order by inv.deliveryDate, inv.invID")
	List<InvoiceDetails> findUndeliveredDueBefore(@Param("before") Date before);

	@Query("select count(inv) from InvoiceDetails inv where inv.deliveryDate >= :fromDate and inv.deliveryDate < :toDate"
			+ " and (inv.orderStatus is null or inv.orderStatus <> 'COLLECTED')"
			+ " and (inv.activeStatus is null or inv.activeStatus <> 0)")
	long countUndeliveredDueBetween(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query("select count(inv) from InvoiceDetails inv where inv.deliveryDate < :before"
			+ " and (inv.orderStatus is null or inv.orderStatus <> 'COLLECTED')"
			+ " and (inv.activeStatus is null or inv.activeStatus <> 0)")
	long countUndeliveredDueBefore(@Param("before") Date before);

	@Query("select inv.orderStatus, count(inv), min(inv.invoicedDate) from InvoiceDetails inv"
			+ " where (inv.orderStatus is null or inv.orderStatus <> 'COLLECTED')"
			+ " and (inv.activeStatus is null or inv.activeStatus <> 0) group by inv.orderStatus")
	List<Object[]> summariseOpenByStatus();

	@Query("select count(inv), coalesce(sum(inv.invoicedAmount), 0) from InvoiceDetails inv"
			+ " where inv.invoicedDate >= :fromDate and inv.invoicedDate < :toDate"
			+ " and (inv.activeStatus is null or inv.activeStatus <> 0)")
	List<Object[]> summariseBilledBetween(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query("select coalesce(sum(inv.outstanding), 0) from InvoiceDetails inv"
			+ " where (inv.activeStatus is null or inv.activeStatus <> 0)")
	double sumOutstanding();

	@Query("From InvoiceDetails inv where inv.deliveryDate >= :fromDate and inv.deliveryDate < :toDate"
			+ " and (inv.orderStatus is null or inv.orderStatus <> 'COLLECTED')"
			+ " and (inv.activeStatus is null or inv.activeStatus <> 0) order by inv.deliveryDate, inv.invID")
	List<InvoiceDetails> findUndeliveredDueBetween(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			Pageable page);

}
