package com.laundry.LaundryManagement.beans;

public class ItemCategoryResponse extends BaseResponse {
	
	private String categoryCode;
	private int id;
	private String categoryName;
	private int activeStatus;
	/**
	 * @return the categoryCode
	 */
	public String getCategoryCode() {
		return categoryCode;
	}
	/**
	 * @param categoryCode the categoryCode to set
	 */
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}
	/**
	 * @param categoryName the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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
	
	
}
