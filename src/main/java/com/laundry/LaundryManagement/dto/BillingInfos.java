package com.laundry.LaundryManagement.dto;

import java.util.List;


public class BillingInfos {

	private int cusID;
	private String deliveryDate; //(dd/MM/yyyy)
	private List<BillingComponets> componets;

	
	
	/**
	 * @return the cusID
	 */
	public int getCusID() {
		return cusID;
	}

	/**
	 * @param cusID the cusID to set
	 */
	public void setCusID(int cusID) {
		this.cusID = cusID;
	}

	/**
	 * @return the componets
	 */
	public List<BillingComponets> getComponets() {
		return componets;
	}

	/**
	 * @param componets the componets to set
	 */
	public void setComponets(List<BillingComponets> componets) {
		this.componets = componets;
	}

	/**
	 * @return the deliveryDate
	 */
	public String getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	
	

	
}
