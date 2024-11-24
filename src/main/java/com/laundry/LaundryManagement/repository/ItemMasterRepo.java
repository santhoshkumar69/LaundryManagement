/**
 * 
 */
package com.laundry.LaundryManagement.repository;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.ItemMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly=false)
public interface ItemMasterRepo extends JpaRepository<ItemMaster, Integer> {
	
	@Query("select itemMaster From ItemMaster itemMaster where itemMaster.activeStatus = 1")
	List<ItemMaster> findAll();
	
	@Modifying
	@Query("update ItemMaster c set c.activeStatus=0 WHERE c.id=:itemId")
	Integer deactivateItemMaster(@Param("itemId") Integer itemId);
	
//	@Override
	@Query("from ItemMaster itemMaster where itemMaster.id=id")
	ItemMaster findByItemID(Integer id);
	
	@Query("select itemMaster from ItemMaster itemMaster where "
			+ "itemMaster.itemName = :itemName ")
	ItemMaster findByItemName(@Param("itemName") String itemName);
	
   @Modifying
	@Query("update ItemMaster cs set cs.activeStatus=1 WHERE cs.id=:itemId")
	Integer activateItemMaster(@Param("itemId") Integer itemId);

	@Query("From ItemMaster itemMasters where itemMasters.activeStatus = 0")
	List<ItemMaster> getAllDeactiveItem();

	@Query("select itemMasterName from ItemMaster itemMasterName where "
			+ "itemMasterName.itemCode = :itemCode ")
	ItemMaster findByItemCode(@Param("itemCode") String itemCode);
	
	@Query("from ItemMaster itemMaster where itemMaster.id = :id ")
	ItemMaster findByIdforDeactive(@Param("id") Integer id);

	Optional<ItemMaster> findById(Integer itemID);

}
