/**
 * 
 */
package com.laundry.LaundryManagement.repository;

import com.laundry.LaundryManagement.model.InvoiceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author pandyarajan
 *
 * 12-Mar-2019
 */
@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetails, Integer>{

}
