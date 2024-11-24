package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.ItemCategory;

public interface ItemCategoryService {
	
	boolean createItemCategory(ItemCategory itemCategory);

	List<ItemCategory> fetchItemCategoryDetails();
	
	void deactivateItemCategory(Integer categoryId);

	ItemCategory findById(Integer id);

	ItemCategory findByCategoryName(String categoryName);

	ItemCategory findByCategoryCode(String code);

	Optional<ItemCategory> findOne(Integer id);
	
	List<ItemCategory> fetchAllDeActiveItemCategory();
}
