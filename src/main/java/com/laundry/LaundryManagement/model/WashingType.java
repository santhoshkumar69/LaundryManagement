package com.laundry.LaundryManagement.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TA_LA_WASHING_TYPE")
public class WashingType implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")	
	private int processTypeId;
	
	@Column(name="WASHING_TYPE")
	private String processTypeName;
	
	@Column(name="PRICE")
	private int price;
	
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
	
//	@OneToMany(mappedBy = "washingType")
//	private List<ItemMasterPriceDetails> theItemMasterPriceDetails;

	public int getProcessTypeId() {
		return processTypeId;
	}

	public String getProcessTypeName() {
		return processTypeName;
	}

	public int getPrice() {
		return price;
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

	public void setProcessTypeId(int processTypeId) {
		this.processTypeId = processTypeId;
	}

	public void setProcessTypeName(String processTypeName) {
		this.processTypeName = processTypeName;
	}

	public void setPrice(int price) {
		this.price = price;
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

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public void setLocationID(String locationID) {
		this.locationID = locationID;
	}

//	/**
//	 * @return the theItemMasterPriceDetails
//	 */
//	public List<ItemMasterPriceDetails> getTheItemMasterPriceDetails() {
//		return theItemMasterPriceDetails;
//	}
//
//	/**
//	 * @param theItemMasterPriceDetails the theItemMasterPriceDetails to set
//	 */
//	public void setTheItemMasterPriceDetails(List<ItemMasterPriceDetails> theItemMasterPriceDetails) {
//		this.theItemMasterPriceDetails = theItemMasterPriceDetails;
//	}

}
