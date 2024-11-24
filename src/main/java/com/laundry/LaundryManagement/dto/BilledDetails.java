/**
 * 
 */
package com.laundry.LaundryManagement.dto;

import java.util.List;

import com.laundry.LaundryManagement.model.BilledComponents;
import com.laundry.LaundryManagement.model.CustomerInfo;

/**
 * @author pandyarajan
 *
 * 12-Mar-2019
 */
public class BilledDetails {

	private int estID;
	private double totalAmt;
	private List<BilledComponents> components;
	private CustomerInfo customerInfo;
	
	/**
	 * @return the estID
	 */
	public int getEstID() {
		return estID;
	}
	/**
	 * @param estID the estID to set
	 */
	public void setEstID(int estID) {
		this.estID = estID;
	}
	/**
	 * @return the components
	 */
	public List<BilledComponents> getComponents() {
		return components;
	}
	/**
	 * @param components the components to set
	 */
	public void setComponents(List<BilledComponents> components) {
		this.components = components;
	}
	/**
	 * @return the totalAmt
	 */
	public double getTotalAmt() {
		return totalAmt;
	}
	/**
	 * @param totalAmt the totalAmt to set
	 */
	public void setTotalAmt(double totalAmt) {
		this.totalAmt = totalAmt;
	}
	/**
	 * @return the customerInfo
	 */
	public CustomerInfo getCustomerInfo() {
		return customerInfo;
	}
	/**
	 * @param customerInfo the customerInfo to set
	 */
	public void setCustomerInfo(CustomerInfo customerInfo) {
		this.customerInfo = customerInfo;
	}
	
}
