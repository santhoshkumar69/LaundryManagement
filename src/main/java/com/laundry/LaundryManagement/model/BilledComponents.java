/**
 * 
 */
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

/**
 * @author pandyarajan
 *
 * 11-Mar-2019
 */
@Entity
@Table(name = "TB_BILLING_COMPONENT")
@Data
public class BilledComponents implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SID")
	private int sID;
	@Column(name = "EST_ID")
	private int estimateID;
	@Column(name = "INVOICE_NO")
	private String invoiceNo;
	@Column(name = "ITEM_ID")
	private int itemID;
	@Column(name = "ITEM_NAME")
	private String itemName;
	@Column(name = "LAUNDRY_AMT")
	private double laundryAmt;
	@Column(name = "IRON_AMT")
	private double IronAmt;
	@Column(name = "QTY")
	private String qty;
	@Column(name = "DISCOUNT_AMT")
	private double DiscountAmt;
	@Column(name = "DISCPUNT_PERCENT")
	private double discountPercent;
	@Column(name = "TOTAL_AMT")
	private double totalAmt;
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
	 * @return the sID
	 */
	public int getsID() {
		return sID;
	}
	/**
	 * @param sID the sID to set
	 */
	public void setsID(int sID) {
		this.sID = sID;
	}
	/**
	 * @return the estimateID
	 */
	public int getEstimateID() {
		return estimateID;
	}
	/**
	 * @param estimateID the estimateID to set
	 */
	public void setEstimateID(int estimateID) {
		this.estimateID = estimateID;
	}
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
	 * @return the laundryAmt
	 */
	public double getLaundryAmt() {
		return laundryAmt;
	}
	/**
	 * @param laundryAmt the laundryAmt to set
	 */
	public void setLaundryAmt(double laundryAmt) {
		this.laundryAmt = laundryAmt;
	}
	/**
	 * @return the ironAmt
	 */
	public double getIronAmt() {
		return IronAmt;
	}
	/**
	 * @param ironAmt the ironAmt to set
	 */
	public void setIronAmt(double ironAmt) {
		IronAmt = ironAmt;
	}
	/**
	 * @return the qty
	 */
	public String getQty() {
		return qty;
	}
	/**
	 * @param qty the qty to set
	 */
	public void setQty(String qty) {
		this.qty = qty;
	}
	/**
	 * @return the discountAmt
	 */
	public double getDiscountAmt() {
		return DiscountAmt;
	}
	/**
	 * @param discountAmt the discountAmt to set
	 */
	public void setDiscountAmt(double discountAmt) {
		DiscountAmt = discountAmt;
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
	 * @return the invoiceNo
	 */
	public String getInvoiceNo() {
		return invoiceNo;
	}
	/**
	 * @param invoiceNo the invoiceNo to set
	 */
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	
}
