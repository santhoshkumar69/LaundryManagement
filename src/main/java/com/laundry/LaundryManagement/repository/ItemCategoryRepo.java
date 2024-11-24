package com.laundry.LaundryManagement.repository;



import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = false)
public interface ItemCategoryRepo extends JpaRepository<ItemCategory, Long>{
	
	@Override
	@Query("From ItemCategory itemCategory where itemCategory.activeStatus=1")
	List<ItemCategory> findAll();
	
	@Modifying
	@Query("update ItemCategory itemCategory set itemCategory.activeStatus=0 WHERE itemCategory.id=:categoryId")
	Integer deactivateItemCategory(@Param("categoryId") Integer categoryId);
	
	@Query("from ItemCategory itemId where itemId.id = :id ")
	ItemCategory findById(@Param("id") Integer id);
	
	@Query("select itemCategory from ItemCategory itemCategory where "
			+ "itemCategory.categoryName = :categoryName ")
	ItemCategory findItemCategoryName(@Param("categoryName") String categoryName);
	
	@Query("select itemCategory from ItemCategory itemCategory where "
			+ "itemCategory.categoryCode = :categoryCode ")
	ItemCategory findCategoryCode(@Param("categoryCode") String categoryCode);

	@Query("from ItemCategory itemId where itemId.id = :id ")
	Optional<ItemCategory> findByIds(@Param("id") Integer id);
	
	@Query("From ItemCategory itemCategory where itemCategory.activeStatus=0")
	List<ItemCategory> getAllDeActiveCategory();
	
	
}
