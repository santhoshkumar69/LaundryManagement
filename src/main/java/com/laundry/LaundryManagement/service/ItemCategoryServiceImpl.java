package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.ItemCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.repository.ItemCategoryRepo;

@Service
public class ItemCategoryServiceImpl implements ItemCategoryService {

	@Autowired
	ItemCategoryRepo itemCategoryRepo;
	
	@Override
	@Transactional
	public boolean createItemCategory(ItemCategory itemCategory) {
		
		try {
			//itemCategory.setActiveStatus(1);
			itemCategoryRepo.save(itemCategory);
			return true;
		}catch (Exception Ex){
       Ex.printStackTrace();
       return false;
		}}
	

	@Override
	@Transactional
	public List<ItemCategory> fetchItemCategoryDetails() {
		return itemCategoryRepo.findAll();
	}

	@Override
	@Transactional
	public void deactivateItemCategory(Integer categoryId) {
		itemCategoryRepo.deactivateItemCategory(categoryId);
		
	}

	@Override
	@Transactional
	public ItemCategory findById(Integer id) {
		return itemCategoryRepo.findById(id);		
		
	}

	@Override
	public ItemCategory findByCategoryName(String categoryName) {

		return itemCategoryRepo.findItemCategoryName(categoryName);
	}

	@Transactional
	public ItemCategory findByCategoryCode(String code) {
	return itemCategoryRepo.findCategoryCode(code);
		
	}


	@Override
	public Optional<ItemCategory> findOne(Integer id) {
		
		return itemCategoryRepo.findByIds(id);
	}


	@Override
	public List<ItemCategory> fetchAllDeActiveItemCategory() {
		
		return itemCategoryRepo.getAllDeActiveCategory();
	}


}
