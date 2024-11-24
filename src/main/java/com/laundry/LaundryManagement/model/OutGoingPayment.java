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
@Table(name = "TB_LA_OUTGOING_PAYMENT")
@Data
public class OutGoingPayment implements Serializable {
	
	public OutGoingPayment()
	{
		activeStatus=1;
	}
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "ENTRY_DATE")
	private Date entryDate;
	
	@Column(name = "INVOICE_NO")
	private Integer invoiceNo;
	
	@Column(name = "ACCOUNT_TYPE")
	private String accountType;
	
	@Column(name = "AMOUNT")
	private Integer amount;
	 
	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "PAYMENT_TYPE")
	private String paymentType; 
	
	@Column(name = "VOID_REASON")
	private String voidReason;
	
	@Column(name="ACTIVE_STATUS",nullable=false)
	private int activeStatus;
	
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

	public Integer getId() {
		return id;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public Integer getInvoiceNo() {
		return invoiceNo;
	}

	public String getAccountType() {
		return accountType;
	}

	public Integer getAmount() {
		return amount;
	}

	public String getRemarks() {
		return remarks;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public int getActiveStatus() {
		return activeStatus;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public String getLocationID() {
		return locationID;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public void setInvoiceNo(Integer invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public void setActiveStatus(int activeStatus) {
		this.activeStatus = activeStatus;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getVoidReason() {
		return voidReason;
	}

	public void setVoidReason(String voidReason) {
		this.voidReason = voidReason;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public void setLocationID(String locationID) {
		this.locationID = locationID;
	}
}
