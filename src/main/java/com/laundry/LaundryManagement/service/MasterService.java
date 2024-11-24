/**
 * 
 */
package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.ItemMaster;
import com.laundry.LaundryManagement.beans.ItemMasterBean;

/**
 * @author pandyarajan
 *
 *         26-Dec-2018
 */
public interface MasterService {

	boolean createItemMaster(ItemMasterBean itemInfoBean);

	List<ItemMasterBean> fetchItemDetails();

	void deactivateItemInfo(Integer itemID);

	ItemMasterBean findOne(Integer id);
	
	Optional<ItemMaster> findById(Integer itemId);
	
	ItemMaster findByItem(String itemname);
	
	ItemMaster findByIdforDeactive(Integer id);
	
	void activateItemMaster(Integer itemId);
	
	List<ItemMasterBean> getDeactiveItems();

	ItemMaster findByItemName(String itemName);
	
	ItemMaster findByItemCode(String ItemCode);
	
	boolean saveItemMaster(ItemMaster itemMaster);
	
}
