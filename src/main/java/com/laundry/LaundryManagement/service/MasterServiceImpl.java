/**
 * 
 */
package com.laundry.LaundryManagement.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.model.ItemCategory;
import com.laundry.LaundryManagement.model.ItemMaster;
import com.laundry.LaundryManagement.model.ItemMasterPriceDetails;
import com.laundry.LaundryManagement.model.Unit;
import com.laundry.LaundryManagement.model.WashingType;
import com.laundry.LaundryManagement.repository.ItemCategoryRepo;
import com.laundry.LaundryManagement.repository.ItemMasterPriceDetailsRepo;
import com.laundry.LaundryManagement.repository.ItemMasterRepo;
import com.laundry.LaundryManagement.repository.UnitRepo;
import com.laundry.LaundryManagement.repository.WashingTypeRepo;
import com.laundry.LaundryManagement.beans.ItemMasterBean;
import com.laundry.LaundryManagement.beans.ItemMasterPriceDetailsBean;

/**
 * @author pandyarajan
 *
 * 26-Dec-2018
 */
@Service
public class MasterServiceImpl implements MasterService{
 
	@Autowired
	ItemMasterRepo itemMasterRepo;
	
	@Autowired
	ItemMasterPriceDetailsRepo itemMasterPriceDetailsRepo;
	
	@Autowired
	UnitRepo unitRepo;
	
	@Autowired
	WashingTypeRepo washingTypeRepo;
	
	@Autowired
	ItemCategoryRepo itemCategoryRepo;

	@Override
	@Transactional
	public boolean createItemMaster(ItemMasterBean itemMasterBean) {
		try{
			itemMasterBean.setActiveStatus(1);
			if(itemMasterBean.getId() == null) {
				ItemMaster itemMaster = new ItemMaster();
				itemMaster.setActiveStatus(1);
				itemMaster.setArabicName(itemMasterBean.getArabicName());
				itemMaster.setItemCategoryid(itemMasterBean.getItemCategoryId());
				itemMaster.setItemCode(itemMasterBean.getItemCode());
				itemMaster.setUserID(itemMasterBean.getUserID());
				itemMaster.setItemName(itemMasterBean.getItemName());
				itemMaster.setFrequentlyUsed(itemMasterBean.isFrequentlyUsed());
				itemMasterRepo.save(itemMaster);
				List<ItemMasterPriceDetailsBean> theItemMasterPriceDetailsBeans = itemMasterBean.getTheItemMasterPriceDetailsBeans();
				this.createItemMasterPriceDetails(theItemMasterPriceDetailsBeans, itemMaster);
				return true;
			} else {
				// To DO for update logic
				ItemMaster itemMaster = itemMasterRepo.findById(itemMasterBean.getId()).orElse(null);
				itemMaster.setActiveStatus(itemMasterBean.getActiveStatus());
				itemMaster.setArabicName(itemMasterBean.getArabicName());
				itemMaster.setFrequentlyUsed(itemMasterBean.isFrequentlyUsed());
				itemMaster.setItemCategoryid(itemMasterBean.getItemCategoryId());
				itemMaster.setItemCode(itemMasterBean.getItemCode());
				itemMaster.setItemName(itemMasterBean.getItemName());
				itemMaster.setUpdatedBy(itemMasterBean.getUserID());
				itemMaster.setUpdatedDate(new Date());
				itemMasterRepo.save(itemMaster);
				List<ItemMasterPriceDetailsBean> theItemMasterPriceDetailsBean = itemMasterBean.getTheItemMasterPriceDetailsBeans();
				for(ItemMasterPriceDetailsBean itemMasterPriceDetailsBean : theItemMasterPriceDetailsBean) {
					ItemMasterPriceDetails itemMasterPriceDetails;
					// if price details bean id is ZERO then create a new row in Item Price Details child table
					if(itemMasterPriceDetailsBean.getId() != 0) {
						itemMasterPriceDetails = itemMasterPriceDetailsRepo.findByID(itemMasterPriceDetailsBean.getId());
					} else {
						itemMasterPriceDetails = new ItemMasterPriceDetails();
					}
					itemMasterPriceDetails.setOrdinaryPrice(itemMasterPriceDetailsBean.getOrdinaryPrice());
					itemMasterPriceDetails.setUnitId(itemMasterPriceDetailsBean.getUnitId());
					itemMasterPriceDetails.setUrgentPrice(itemMasterPriceDetailsBean.getUrgentPrice());
					itemMasterPriceDetails.setWashingTypeId(itemMasterPriceDetailsBean.getWashingTypeId());
					itemMasterPriceDetails.setItemMasterId(itemMaster.getId());
					itemMasterPriceDetailsRepo.save(itemMasterPriceDetails);
				}
				
				return true;
			}
			
		} catch (Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	
	@Override
	@Transactional
	public boolean saveItemMaster(ItemMaster itemMaster) {
	
		try {
			itemMasterRepo.save(itemMaster);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}

	
	public boolean createItemMasterPriceDetails(List<ItemMasterPriceDetailsBean> theItemMasterPriceDetailsBean, ItemMaster itemMaster) {
		
		try {
			if(theItemMasterPriceDetailsBean != null) {
				for(ItemMasterPriceDetailsBean itemMasterPriceDetailsBean : theItemMasterPriceDetailsBean) {
					ItemMasterPriceDetails itemMasterPriceDetails = new ItemMasterPriceDetails();
					
					itemMasterPriceDetails.setItemMasterId(itemMaster.getId());
					itemMasterPriceDetails.setOrdinaryPrice(itemMasterPriceDetailsBean.getOrdinaryPrice());
					itemMasterPriceDetails.setWashingTypeId(itemMasterPriceDetailsBean.getWashingTypeId());
					itemMasterPriceDetails.setUrgentPrice(itemMasterPriceDetailsBean.getUrgentPrice());
					itemMasterPriceDetails.setUnitId(itemMasterPriceDetailsBean.getUnitId());
					
					itemMasterPriceDetailsRepo.save(itemMasterPriceDetails);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	@Transactional
	public List<ItemMasterBean> fetchItemDetails() {
		
		List<ItemMasterBean> theItemMasterBeans = new ArrayList<ItemMasterBean>();
		List<ItemMaster> theItemMasters = itemMasterRepo.findAll();
		for(ItemMaster itemMaster : theItemMasters) {
			List<ItemMasterPriceDetailsBean> theItemMasterPriceDetailsBeans = new ArrayList<ItemMasterPriceDetailsBean>();
			ItemMasterBean itemMasterBean = new ItemMasterBean();
			itemMasterBean.setActiveStatus(itemMaster.getActiveStatus());
			itemMasterBean.setArabicName(itemMaster.getArabicName());
			itemMasterBean.setId(itemMaster.getId());
			itemMasterBean.setItemCode(itemMaster.getItemCode());
			itemMasterBean.setItemName(itemMaster.getItemName());
			itemMasterBean.setUserID(itemMaster.getUserID());
			itemMasterBean.setFrequentlyUsed(itemMaster.isFrequentlyUsed());
			itemMasterBean.setItemCategoryId(itemMaster.getItemCategoryid());
			ItemCategory category = itemCategoryRepo.findById(itemMaster.getItemCategoryid());
			itemMasterBean.setItemCategoryName(category == null ? null : category.getCategoryName());
			
			List<ItemMasterPriceDetails> theItemMasterPriceDetails = itemMasterPriceDetailsRepo.findAllByItemMasterId(itemMaster.getId());
			for(ItemMasterPriceDetails itemMasterPriceDetails : theItemMasterPriceDetails) {
				ItemMasterPriceDetailsBean itemMasterPriceDetailsBean = new ItemMasterPriceDetailsBean();
				itemMasterPriceDetailsBean.setId(itemMasterPriceDetails.getID());
				itemMasterPriceDetailsBean.setOrdinaryPrice(itemMasterPriceDetails.getOrdinaryPrice());
				itemMasterPriceDetailsBean.setUrgentPrice(itemMasterPriceDetails.getUrgentPrice());
				itemMasterPriceDetailsBean.setUnitId(itemMasterPriceDetails.getUnitId());
				Unit unit = unitRepo.findByUnit(itemMasterPriceDetails.getUnitId());
				itemMasterPriceDetailsBean.setUnit(unit.getUnit());
				WashingType washingType = washingTypeRepo.findByIds(itemMasterPriceDetails.getWashingTypeId());
				itemMasterPriceDetailsBean.setWashingTypeId(itemMasterPriceDetails.getWashingTypeId());
				itemMasterPriceDetailsBean.setWashingType(washingType.getProcessTypeName());
				itemMasterPriceDetailsBean.setItemMasterId(itemMaster.getId());
				theItemMasterPriceDetailsBeans.add(itemMasterPriceDetailsBean);
			}
			itemMasterBean.setTheItemMasterPriceDetailsBeans(theItemMasterPriceDetailsBeans);
			theItemMasterBeans.add(itemMasterBean);
		}
		
		return theItemMasterBeans;
	}

	@Override
	@Transactional
	public void deactivateItemInfo(Integer itemID) {
		itemMasterRepo.deactivateItemMaster(itemID);
		
	}
	
	@Transactional
	public Optional<ItemMaster> findById(Integer itemId) {
		return itemMasterRepo.findById(itemId);
	}
	
	@Transactional
	public ItemMasterBean findOne(Integer itemID) {
		
		ItemMaster itemMaster = itemMasterRepo.findById(itemID).orElse(null);
		return this.itemMasterToItemMasterBean(itemMaster);
		
	}
	
	private ItemMasterBean itemMasterToItemMasterBean(ItemMaster itemMaster) {
		List<ItemMasterPriceDetailsBean> theItemMasterPriceDetailsBeans = new ArrayList<ItemMasterPriceDetailsBean>();
		ItemMasterBean itemMasterBean = new ItemMasterBean();
		itemMasterBean.setActiveStatus(itemMaster.getActiveStatus());
		itemMasterBean.setArabicName(itemMaster.getArabicName());
		itemMasterBean.setId(itemMaster.getId());
		itemMasterBean.setItemCode(itemMaster.getItemCode());
		itemMasterBean.setItemName(itemMaster.getItemName());
		itemMasterBean.setUserID(itemMaster.getUserID());
		itemMasterBean.setFrequentlyUsed(itemMaster.isFrequentlyUsed());
		List<ItemMasterPriceDetails> theItemMasterPriceDetails = itemMasterPriceDetailsRepo.findAllByItemMasterId(itemMaster.getId());
		for(ItemMasterPriceDetails itemMasterPriceDetails : theItemMasterPriceDetails) {
			ItemMasterPriceDetailsBean itemMasterPriceDetailsBean = new ItemMasterPriceDetailsBean();
			itemMasterPriceDetailsBean.setId(itemMasterPriceDetails.getID());
			itemMasterPriceDetailsBean.setOrdinaryPrice(itemMasterPriceDetails.getOrdinaryPrice());
			itemMasterPriceDetailsBean.setUrgentPrice(itemMasterPriceDetails.getUrgentPrice());
			itemMasterPriceDetailsBean.setUnitId(itemMasterPriceDetails.getUnitId());
			Unit unit = unitRepo.findByUnit(itemMasterPriceDetails.getUnitId());
			itemMasterPriceDetailsBean.setUnit(unit.getUnit());
			WashingType washingType = washingTypeRepo.findByIds(itemMasterPriceDetails.getWashingTypeId());
			itemMasterPriceDetailsBean.setWashingTypeId(itemMasterPriceDetails.getWashingTypeId());
			itemMasterPriceDetailsBean.setWashingType(washingType.getProcessTypeName());
			itemMasterPriceDetailsBean.setItemMasterId(itemMaster.getId());
			theItemMasterPriceDetailsBeans.add(itemMasterPriceDetailsBean);
		}
		itemMasterBean.setTheItemMasterPriceDetailsBeans(theItemMasterPriceDetailsBeans);
		return itemMasterBean;
	}

	@Override
	@Transactional
	public ItemMaster findByItem(String itemName) {
		
		return itemMasterRepo.findByItemName(itemName);
	}

	@Override
	@Transactional
	public void activateItemMaster(Integer itemId) {
		
		 itemMasterRepo.activateItemMaster(itemId);
		
	}

	@Override
	@Transactional
	public List<ItemMasterBean> getDeactiveItems() {
		
		List<ItemMaster> theItemMasters = itemMasterRepo.getAllDeactiveItem();
		List<ItemMasterBean> theItemMasterBeans = new ArrayList<ItemMasterBean>();
		for(ItemMaster itemMaster : theItemMasters) {
			theItemMasterBeans.add(this.itemMasterToItemMasterBean(itemMaster));
		}
		return theItemMasterBeans;
	}

	@Override
	public ItemMaster findByItemName(String itemName) {
		
		return itemMasterRepo.findByItemName(itemName);
	}

	@Override
	public ItemMaster findByItemCode(String ItemCode) {
		
		return itemMasterRepo.findByItemCode(ItemCode);
	}

	@Override
	public ItemMaster findByIdforDeactive(Integer id) {
	
		return itemMasterRepo.findByIdforDeactive(id);
	}
	
	
	
}
