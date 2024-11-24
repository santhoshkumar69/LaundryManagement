package com.laundry.LaundryManagement.beans;

public class ItemMasterPriceDetailsBean {
	
    private int id;
	private int washingTypeId;
	private int unitId;
	private int ordinaryPrice;
	private int urgentPrice;
	private int itemMasterId;
	private String washingType;
	private String unit;
	
	
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
	 * @return the washingTypeId
	 */
	public int getWashingTypeId() {
		return washingTypeId;
	}
	/**
	 * @param washingTypeId the washingTypeId to set
	 */
	public void setWashingTypeId(int washingTypeId) {
		this.washingTypeId = washingTypeId;
	}
	/**
	 * @return the unitId
	 */
	public int getUnitId() {
		return unitId;
	}
	/**
	 * @param unitId the unitId to set
	 */
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}
	/**
	 * @return the ordinaryPrice
	 */
	public int getOrdinaryPrice() {
		return ordinaryPrice;
	}
	/**
	 * @param ordinaryPrice the ordinaryPrice to set
	 */
	public void setOrdinaryPrice(int ordinaryPrice) {
		this.ordinaryPrice = ordinaryPrice;
	}
	/**
	 * @return the urgentPrice
	 */
	public int getUrgentPrice() {
		return urgentPrice;
	}
	/**
	 * @param urgentPrice the urgentPrice to set
	 */
	public void setUrgentPrice(int urgentPrice) {
		this.urgentPrice = urgentPrice;
	}
	/**
	 * @return the itemMasterId
	 */
	public int getItemMasterId() {
		return itemMasterId;
	}
	/**
	 * @param itemMasterId the itemMasterId to set
	 */
	public void setItemMasterId(int itemMasterId) {
		this.itemMasterId = itemMasterId;
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

	
}
