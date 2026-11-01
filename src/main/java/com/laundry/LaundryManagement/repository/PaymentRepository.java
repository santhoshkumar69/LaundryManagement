/**
 * 
 */
package com.laundry.LaundryManagement.repository;

import java.util.List;

import com.laundry.LaundryManagement.model.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
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

}
