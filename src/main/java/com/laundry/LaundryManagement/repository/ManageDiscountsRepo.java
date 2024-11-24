package com.laundry.LaundryManagement.repository;

import java.util.List;

import com.laundry.LaundryManagement.model.Discounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = false)
public interface ManageDiscountsRepo extends JpaRepository<Discounts, Long> {
	
	Discounts findByDiscountName(String discountName);
	
	@Query("From Discounts discounts where discounts.activeStatus = 0")
	List<Discounts> findInactiveDiscounts();
	
	@Query("From Discounts discounts where discounts.activeStatus = 1")
	List<Discounts> findActiveDiscounts();

}
