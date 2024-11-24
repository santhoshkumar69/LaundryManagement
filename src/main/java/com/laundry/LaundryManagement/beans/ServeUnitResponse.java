package com.laundry.LaundryManagement.beans;

public class ServeUnitResponse extends BaseResponse {
	
	private String unit;
	private int id;
	private int activeStatus;
	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
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
