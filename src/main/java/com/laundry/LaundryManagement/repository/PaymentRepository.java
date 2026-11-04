/**
 * 
 */
package com.laundry.LaundryManagement.repository;

import java.util.Date;
import java.util.List;

import com.laundry.LaundryManagement.model.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author pandyarajan
 *
 * 12-Mar-2019
 */
public interface PaymentRepository extends JpaRepository<PaymentDetails, Integer>{

	@Query("From PaymentDetails pay where pay.invoiceNumber = :invNo order by pay.payID")
	List<PaymentDetails> findByInvoiceNumber(@Param("invNo") String invNo);

	@Query("select pay.paidMode, sum(pay.paidAmount) from PaymentDetails pay"
			+ " where pay.payDate >= :fromDate and pay.payDate < :toDate and pay.paidAmount > 0"
			+ " and (pay.payStatus is null or pay.payStatus <> 'RECALLED')"
			+ " and pay.invoiceNumber in (select inv.invoiceNumber from InvoiceDetails inv"
			+ " where inv.activeStatus is null or inv.activeStatus <> 0)"
			+ " group by pay.paidMode")
	List<Object[]> sumReceivedByModeBetween(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Modifying
	@Query("delete from PaymentDetails pay where pay.invoiceNumber = :invNo")
	int deleteByInvoiceNumber(@Param("invNo") String invNo);

}
