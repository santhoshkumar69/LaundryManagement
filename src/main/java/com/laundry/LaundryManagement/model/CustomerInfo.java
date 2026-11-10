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

@Entity
@Table(name = "TB_LA_CUSTOMER_INFO")
@Data
@Filter(name = ScopeFilters.TENANT)
@EntityListeners(ScopeStampingListener.class)
public class CustomerInfo implements Serializable {
	
	public CustomerInfo() {
		activeStatus=1;
	}

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name="QATAR_ID")
	private Long qatarId;
	
	@Column(name="CUSTOMER_NAME")
	private String customerName;
		
	@Column(name="MOBILE_NO")
	private String MobileNo;	
	
	@Column(name="REMARKS")
	private String remarks;
	
	@Column(name="ADDRESS")
	private String address;
	
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

	public Integer getId() {
		return id;
	}

	public Long getQatarId() {
		return qatarId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getRemarks() {
		return remarks;
	}

	public String getAddress() {
		return address;
	}
	
	public int getActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(int activeStatus) {
		this.activeStatus = activeStatus;
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

	public void setQatarId(Long qatarId) {
		this.qatarId = qatarId;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getMobileNo() {
		return MobileNo;
	}

	public void setMobileNo(String mobileNo) {
		MobileNo = mobileNo;
	}
	
	
}
