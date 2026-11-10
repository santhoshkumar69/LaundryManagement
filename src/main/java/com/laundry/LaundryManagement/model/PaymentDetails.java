/**
 * 
 */
package com.laundry.LaundryManagement.model;

import org.hibernate.annotations.Filter;
import jakarta.persistence.EntityListeners;
import com.laundry.LaundryManagement.auth.ScopeFilters;
import com.laundry.LaundryManagement.auth.ScopeStampingListener;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author pandyarajan
 *
 * 12-Mar-2019
 */
@Entity
@Table(name = "TB_PAYMENT_DETAILS")
@Data
@Filter(name = ScopeFilters.TENANT)
@Filter(name = ScopeFilters.OUTLET)
@EntityListeners(ScopeStampingListener.class)
public class PaymentDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PAY_ID")
	private int payID;
	@Column(name = "RECEIPT_NUM")
	private String receiptNumber;
	@Column(name = "INVOICE_NUM")
	private String invoiceNumber;
	@Column(name = "PAID_AMT")
	private double paidAmount;
	@Column(name = "PAID_MODE")
	private String paidMode;
	@Column(name = "DISCOUNT_AMT")
	private double discountAmt;
	@Column(name = "DISCOUNT_PERCENT")
	private double discountPercent;
	@Column(name = "PAY_DATE")
	private Date payDate;
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
	 * @return the payID
	 */
	public int getPayID() {
		return payID;
	}
	/**
	 * @param payID the payID to set
	 */
	public void setPayID(int payID) {
		this.payID = payID;
	}
	/**
	 * @return the receiptNumber
	 */
	public String getReceiptNumber() {
		return receiptNumber;
	}
	/**
	 * @param receiptNumber the receiptNumber to set
	 */
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
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
	 * @return the payDate
	 */
	public Date getPayDate() {
		return payDate;
	}
	/**
	 * @param payDate the payDate to set
	 */
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
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
	 * @return the paidMode
	 */
	public String getPaidMode() {
		return paidMode;
	}
	/**
	 * @param paidMode the paidMode to set
	 */
	public void setPaidMode(String paidMode) {
		this.paidMode = paidMode;
	}

	/** Null means the payment counts; RECALLED means it was reversed and no longer counts. */
	@Column(name = "PAY_STATUS", length = 20)
	private String payStatus;

	@Column(name = "RECALLED_DATE")
	private Date recalledDate;

	@Column(name = "RECALLED_BY")
	private String recalledBy;

	@Column(name = "RECALL_REASON", length = 255)
	private String recallReason;

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public Date getRecalledDate() {
		return recalledDate;
	}

	public void setRecalledDate(Date recalledDate) {
		this.recalledDate = recalledDate;
	}

	public String getRecalledBy() {
		return recalledBy;
	}

	public void setRecalledBy(String recalledBy) {
		this.recalledBy = recalledBy;
	}

	public String getRecallReason() {
		return recallReason;
	}

	public void setRecallReason(String recallReason) {
		this.recallReason = recallReason;
	}

}
