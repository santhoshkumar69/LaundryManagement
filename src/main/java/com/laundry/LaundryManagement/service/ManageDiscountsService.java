package com.laundry.LaundryManagement.service;

import java.util.List;

import com.laundry.LaundryManagement.model.Discounts;

public interface ManageDiscountsService {
	
	public boolean createDiscounts(Discounts discounts) throws Exception;
	
	public List<Discounts> findAll();
	
	public List<Discounts> findInactiveDiscounts();
	
	public List<Discounts> activateDiscounts(Long[] ids);
	
	public List<Discounts> deActivateDiscounts(Long[] ids);
	
	public List<Discounts> findActiveDiscounts();

}
