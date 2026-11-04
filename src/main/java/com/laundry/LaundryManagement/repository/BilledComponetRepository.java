/**
 * 
 */
package com.laundry.LaundryManagement.repository;

import java.util.List;

import com.laundry.LaundryManagement.model.BilledComponents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author pandyarajan
 *
 * 12-Mar-2019
 */
@Repository
@Transactional(readOnly=false)
public interface BilledComponetRepository extends JpaRepository<BilledComponents, Integer>{

	@Query("From BilledComponents compo where compo.estimateID = :estID")
	List<BilledComponents> fetchBillingCompoByEsID(@Param("estID") int estID);

	@Query("From BilledComponents compo where compo.invoiceNo = :invNo")
	List<BilledComponents> fetchBillingCompoByInvoiceNo(@Param("invNo") String invNo);

	@Modifying
	@Query("update BilledComponents compo set compo.invoiceNo = :invNo where compo.estimateID = :estID")
	Integer updateInvoiceID(@Param("invNo") String invNo, @Param("estID") int estID);

	@Modifying
	@Query("delete BilledComponents compo where compo.estimateID = :estID")
	Integer deleteAddedComponents(@Param("estID") int estID);

	@Modifying
	@Query("delete BilledComponents compo where compo.invoiceNo = :invNo")
	Integer deleteByInvoiceNo(@Param("invNo") String invNo);

	//void deleteById(int itemID);

}
