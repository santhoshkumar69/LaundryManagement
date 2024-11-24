package com.laundry.LaundryManagement.model;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "TB_LA_UNIT")
@Data
public class Unit implements Serializable {


	Unit()
	 {
    	activeStatus = 1;
	}
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	
	@Column(name = "UNIT",nullable=false,unique=true)
	private String unit;
	
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
	
	@Column(name = "TENANT_ID")
	private int tenantId;
	
//	@OneToMany(mappedBy = "unit")
//	private List<ItemMasterPriceDetails> theItemMasterPriceDetails;

	public Integer getId() {
		return id;
	}

	public String getUnit() {
		return unit;
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

	public void setUnit(String unit) {
		this.unit = unit;
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


}
