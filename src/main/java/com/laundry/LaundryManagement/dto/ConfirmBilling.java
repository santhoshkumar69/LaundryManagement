/**
 * 
 */
package com.laundry.LaundryManagement.dto;

/**
 * @author pandyarajan
 *
 *         12-Mar-2019
 */
public class ConfirmBilling {

	private int estID;
	private int cusID;
	private double invoicedAmt;
	private double discountAmt;
	private double discountPercent;
	private double amountPay;
	private double paidAmount;
	private String payMode;
	private String comments;

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
	 * @return the invoicedAmt
	 */
	public double getInvoicedAmt() {
		return invoicedAmt;
	}

	/**
	 * @param invoicedAmt the invoicedAmt to set
	 */
	public void setInvoicedAmt(double invoicedAmt) {
		this.invoicedAmt = invoicedAmt;
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
	 * @return the discountPercent
	 */
	public double getDiscountPercent() {
		return discountPercent;
	}

	/**
	 * @param discountPercent the discountPercent to set
	 */
	public void setDiscountPercent(double discountPercent) {
		this.discountPercent = discountPercent;
	}

	/**
	 * @return the amountPay
	 */
	public double getAmountPay() {
		return amountPay;
	}

	/**
	 * @param amountPay the amountPay to set
	 */
	public void setAmountPay(double amountPay) {
		this.amountPay = amountPay;
	}

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
	 * @return the paidAmount
	 */
	public double getPaidAmount() {
		return paidAmount;
	}

	/**
	 * @param paidAmount the paidAmount to set
	 */
	public void setPaidAmount(double paidAmount) {
		this.paidAmount = paidAmount;
	}

	/**
	 * @return the payMode
	 */
	public String getPayMode() {
		return payMode;
	}

	/**
	 * @param payMode the payMode to set
	 */
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	

}
