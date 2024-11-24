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
public class ReceiptDetailDto {
	
	private String receiptNo;
	private double paidAmt;
	private double discountAmt;
	private double outstanding;
	private CustomerInfo customer;
	private List<BilledComponents> components;
	
	/**
	 * @return the receiptNo
	 */
	public String getReceiptNo() {
		return receiptNo;
	}
	/**
	 * @param receiptNo the receiptNo to set
	 */
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	/**
	 * @return the paidAmt
	 */
	public double getPaidAmt() {
		return paidAmt;
	}
	/**
	 * @param paidAmt the paidAmt to set
	 */
	public void setPaidAmt(double paidAmt) {
		this.paidAmt = paidAmt;
	}
	/**
	 * @return the discountAmt
	 */
	public double getDiscountAmt() {
		return discountAmt;
	}
	/**
	 * @param discountAmt the discountAmt to set
	 */
	public void setDiscountAmt(double discountAmt) {
		this.discountAmt = discountAmt;
	}
	/**
	 * @return the outstanding
	 */
	public double getOutstanding() {
		return outstanding;
	}
	/**
	 * @param outstanding the outstanding to set
	 */
	public void setOutstanding(double outstanding) {
		this.outstanding = outstanding;
	}
	/**
	 * @return the customer
	 */
	public CustomerInfo getCustomer() {
		return customer;
	}
	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(CustomerInfo customer) {
		this.customer = customer;
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

}
