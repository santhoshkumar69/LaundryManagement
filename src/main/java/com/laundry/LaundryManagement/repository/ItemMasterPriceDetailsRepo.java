package com.laundry.LaundryManagement.repository;

import java.util.List;

import com.laundry.LaundryManagement.model.ItemMasterPriceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly=false)
public interface ItemMasterPriceDetailsRepo extends JpaRepository<ItemMasterPriceDetails, Integer>  {

	@Override
	@Query("From ItemMasterPriceDetails itemMasterPriceDetails")
	List<ItemMasterPriceDetails> findAll();
	
	@Query("From ItemMasterPriceDetails itemMasterPriceDetails where itemMasterPriceDetails.itemMasterId = :id ")
	List<ItemMasterPriceDetails> findAllByItemMasterId(int id);
	
	@Query("From ItemMasterPriceDetails itemMasterPriceDetails where itemMasterPriceDetails.ID = :ID ")
	ItemMasterPriceDetails findByID(Integer ID);
}
