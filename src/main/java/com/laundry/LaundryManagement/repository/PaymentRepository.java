/**
 * 
 */
package com.laundry.LaundryManagement.repository;

import com.laundry.LaundryManagement.model.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author pandyarajan
 *
 * 12-Mar-2019
 */
public interface PaymentRepository extends JpaRepository<PaymentDetails, Integer>{

}
