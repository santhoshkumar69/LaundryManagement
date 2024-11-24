package com.laundry.LaundryManagement.dto;

public class BillingComponets {

	private int itemID;
	private String itemName;
	private int qty;
	private boolean isUrgent;
	private boolean ironReq;
	private boolean laundryReq;
	
	/**
	 * @return the itemID
	 */
	public int getItemID() {
		return itemID;
	}
	/**
	 * @param itemID the itemID to set
	 */
	public void setItemID(int itemID) {
		this.itemID = itemID;
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
	 * @return the qty
	 */
	public int getQty() {
		return qty;
	}
	/**
	 * @param qty the qty to set
	 */
	public void setQty(int qty) {
		this.qty = qty;
	}
	/**
	 * @return the isUrgent
	 */
	public boolean isUrgent() {
		return isUrgent;
	}
	/**
	 * @param isUrgent the isUrgent to set
	 */
	public void setUrgent(boolean isUrgent) {
		this.isUrgent = isUrgent;
	}
	
	public boolean isIronReq() {
		return ironReq;
	}
	public boolean isLaundryReq() {
		return laundryReq;
	}
	public void setIronReq(boolean ironReq) {
		this.ironReq = ironReq;
	}
	public void setLaundryReq(boolean laundryReq) {
		this.laundryReq = laundryReq;
	}
	
	
	
}
