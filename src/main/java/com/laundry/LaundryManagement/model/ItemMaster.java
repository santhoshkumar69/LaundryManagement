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
 *         26-Dec-2018
 */

@Entity
@Table(name = "TB_LA_ITEM_MASTER")
@Data
public class ItemMaster implements Serializable {
  
	public ItemMaster()
	{
		activeStatus = 1;
	}

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ITEM_ID")
	private Integer id;
	
	@Column(name="ACTIVE_STATUS",nullable=false)
	private int activeStatus;
	
	@Column(name = "CATEGORY_ID")
	private int itemCategoryid;
	
	@Column(name = "ITEM_CODE")
	private String itemCode; 	
	
	@Column(name = "ITEM_NAME")
	private String itemName;
	
	@Column(name = "IRONING_RATE")
	private Double ironingRate;
	
	@Column(name = "ITEM_ALT_NAME")
	private String arabicName;

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
	
	@Column(name = "FREQUENTLY_USED")
	private boolean frequentlyUsed;
	
	@Column(name = "TENANT_ID")
	private int tenantId;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	
	/**
	 * @return the itemCategoryid
	 */
	public int getItemCategoryid() {
		return itemCategoryid;
	}

	/**
	 * @param itemCategoryid the itemCategoryid to set
	 */
	public void setItemCategoryid(int itemCategoryid) {
		this.itemCategoryid = itemCategoryid;
	}

	/**
	 * @return the itemCode
	 */
	public String getItemCode() {
		return itemCode;
	}

	/**
	 * @param itemCode the itemCode to set
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
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
	 * @return the arabicName
	 */
	public String getArabicName() {
		return arabicName;
	}

	/**
	 * @param arabicName the arabicName to set
	 */
	public void setArabicName(String arabicName) {
		this.arabicName = arabicName;
	}
	/**
	 * @return the activeStatus
	 */
	public int getActiveStatus() {
		return activeStatus;
	}

	/**
	 * @param activeStatus the activeStatus to set
	 */
	public void setActiveStatus(int activeStatus) {
		this.activeStatus = activeStatus;
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
	 * @return the frequentlyUsed
	 */
	public boolean isFrequentlyUsed() {
		return frequentlyUsed;
	}

	/**
	 * @param frequentlyUsed the frequentlyUsed to set
	 */
	public void setFrequentlyUsed(boolean frequentlyUsed) {
		this.frequentlyUsed = frequentlyUsed;
	}
	
	
	

}
