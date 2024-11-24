package com.laundry.LaundryManagement.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "TB_INVOICE_DETAILS")
@Data
public class InvoiceDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "INV_ID")
	private int invID;
	@Column(name = "INVOICE_NO")
	private String invoiceNumber;
	@Column(name = "INVOICE_DATE")
	private Date invoicedDate;
	@Column(name = "CUS_ID")
	private int cusID;
	@Column(name = "INVOICED_AMT")
	private double invoicedAmount;
	@Column(name = "PAID_AMT")
	private double paidAmount;
	@Column(name = "OUTSTANDING_AMT")
	private double outstanding;
	@Column(name = "ADVANCE_AMT")
	private double advanceAmt;
	@Column(name = "DISCOUNT_AMT")
	private double discountAmount;
	@Column(name = "DISCOUNT_PERCENT")
	private double discountPercent;
	@Column(name = "CREATED_BY")
	private String createdBy;
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;
	@Column(name = "LOCATION_ID")
	private String locationID;
	@Column(name = "USER_ID")
	private String userID;
	
	@Column(name = "TENANT_ID")
	private int tenantId;
	
	/**
	 * @return the invID
	 */
	public int getInvID() {
		return invID;
	}
	/**
	 * @param invID the invID to set
	 */
	public void setInvID(int invID) {
		this.invID = invID;
	}
	/**
	 * @return the invoiceNumber
	 */
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	/**
	 * @param invoiceNumber the invoiceNumber to set
	 */
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
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
	 * @return the invoicedAmount
	 */
	public double getInvoicedAmount() {
		return invoicedAmount;
	}
	/**
	 * @param invoicedAmount the invoicedAmount to set
	 */
	public void setInvoicedAmount(double invoicedAmount) {
		this.invoicedAmount = invoicedAmount;
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
	 * @return the advanceAmt
	 */
	public double getAdvanceAmt() {
		return advanceAmt;
	}
	/**
	 * @param advanceAmt the advanceAmt to set
	 */
	public void setAdvanceAmt(double advanceAmt) {
		this.advanceAmt = advanceAmt;
	}
	/**
	 * @return the discountAmount
	 */
	public double getDiscountAmount() {
		return discountAmount;
	}
	/**
	 * @param discountAmount the discountAmount to set
	 */
	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
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
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}
	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	/**
	 * @return the updatedDate
	 */
	public Date getUpdatedDate() {
		return updatedDate;
	}
	/**
	 * @param updatedDate the updatedDate to set
	 */
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	/**
	 * @return the locationID
	 */
	public String getLocationID() {
		return locationID;
	}
	/**
	 * @param locationID the locationID to set
	 */
	public void setLocationID(String locationID) {
		this.locationID = locationID;
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
	 * @return the invoicedDate
	 */
	public Date getInvoicedDate() {
		return invoicedDate;
	}
	/**
	 * @param invoicedDate the invoicedDate to set
	 */
	public void setInvoicedDate(Date invoicedDate) {
		this.invoicedDate = invoicedDate;
	}
	
	
	
}
