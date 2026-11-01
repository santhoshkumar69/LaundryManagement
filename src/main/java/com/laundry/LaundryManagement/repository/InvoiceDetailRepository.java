/**
 * 
 */
package com.laundry.LaundryManagement.repository;

import java.util.Date;
import java.util.List;

import com.laundry.LaundryManagement.model.InvoiceDetails;
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

}
