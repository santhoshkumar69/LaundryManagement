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

/**
 * @author pandyarajan
 *
 * 12-Mar-2019
 */
//@Repository
//@Transactional(readOnly=false)
public interface BilledComponetRepository extends JpaRepository<BilledComponents, Integer>{

	@Query("From BilledComponents compo where compo.estimateID = :estID")
	List<BilledComponents> fetchBillingCompoByEsID(@Param("estID") int estID);

	@Modifying
	@Query("update BilledComponents compo set compo.invoiceNo = :invNo where compo.estimateID = :estID")
	Integer updateInvoiceID(@Param("invNo") String invNo, @Param("estID") int estID);

	@Query("delete BilledComponents compo where compo.estimateID = :estID")
	Integer deleteAddedComponents(@Param("estID") int estID);

	//void deleteById(int itemID);

}
