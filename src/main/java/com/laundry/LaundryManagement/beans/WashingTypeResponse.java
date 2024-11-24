package com.laundry.LaundryManagement.beans;

public class WashingTypeResponse extends BaseResponse {
	
	private int id;
	private String washingType;
	private int activeStatus;
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
	 * @return the washingType
	 */
	public String getWashingType() {
		return washingType;
	}
	/**
	 * @param washingType the washingType to set
	 */
	public void setWashingType(String washingType) {
		this.washingType = washingType;
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
