package com.laundry.LaundryManagement.beans;

import java.util.List;

public class ItemMasterBean extends BaseResponse {
	
	private Integer id;
	private int itemCategoryId;
	private String itemCode;
	private String itemName;
	private String arabicName;
	private int activeStatus;
	private String userID;
	private boolean frequentlyUsed;
	private String itemCategoryName;
	private List<ItemMasterPriceDetailsBean> theItemMasterPriceDetailsBeans;
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the itemCategoryId
	 */
	public int getItemCategoryId() {
		return itemCategoryId;
	}
	/**
	 * @param itemCategoryId the itemCategoryId to set
	 */
	public void setItemCategoryId(int itemCategoryId) {
		this.itemCategoryId = itemCategoryId;
	}
	/**
	 * @return the itemCode
	 */
	public String getItemCode() {
		return itemCode;
	}
	/**
	 * @param itemCode the itemCode to set
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}
	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	/**
	 * @return the arabicName
	 */
	public String getArabicName() {
		return arabicName;
	}
	/**
	 * @param arabicName the arabicName to set
	 */
	public void setArabicName(String arabicName) {
		this.arabicName = arabicName;
	}
	/**
	 * @return the activeStatus
	 */
	public int getActiveStatus() {
		return activeStatus;
	}
	/**
	 * @param activeStatus the activeStatus to set
	 */
	public void setActiveStatus(int activeStatus) {
		this.activeStatus = activeStatus;
	}
	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}
	/**
	 * @param userID the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}
	/**
	 * @return the theItemMasterPriceDetailsBeans
	 */
	public List<ItemMasterPriceDetailsBean> getTheItemMasterPriceDetailsBeans() {
		return theItemMasterPriceDetailsBeans;
	}
	/**
	 * @param theItemMasterPriceDetailsBeans the theItemMasterPriceDetailsBeans to set
	 */
	public void setTheItemMasterPriceDetailsBeans(List<ItemMasterPriceDetailsBean> theItemMasterPriceDetailsBeans) {
		this.theItemMasterPriceDetailsBeans = theItemMasterPriceDetailsBeans;
	}
	/**
	 * @return the frequentlyUsed
	 */
	public boolean isFrequentlyUsed() {
		return frequentlyUsed;
	}
	/**
	 * @param frequentlyUsed the frequentlyUsed to set
	 */
	public void setFrequentlyUsed(boolean frequentlyUsed) {
		this.frequentlyUsed = frequentlyUsed;
	}
	/**
	 * @return the itemCategoryName
	 */
	public String getItemCategoryName() {
		return itemCategoryName;
	}
	/**
	 * @param itemCategoryName the itemCategoryName to set
	 */
	public void setItemCategoryName(String itemCategoryName) {
		this.itemCategoryName = itemCategoryName;
	}
	
	
	

}
