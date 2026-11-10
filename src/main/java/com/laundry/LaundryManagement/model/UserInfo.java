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
import jakarta.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Entity
@Table(name = "TB_LA_USER_INFO", uniqueConstraints = @UniqueConstraint(columnNames = { "TENANT_ID", "USER_ID" }))
@Data
@Filter(name = ScopeFilters.TENANT)
@EntityListeners(ScopeStampingListener.class)
public class UserInfo implements Serializable{
	
	public UserInfo()
	{
		activeStatus = 1;
	}

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "USER_NAME",  nullable = false)
	private String userName;
	
	@Column(name = "USER_ID",  nullable = false)
	private Long userId;
	
	@Column(name = "PASSWORD",  nullable = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	
	@Column(name = "ROLE",  nullable = false)
	private String role;
	
	@Column(name = "GENDER")
	private String gender;
	
	@Column(name = "BLOOD_GROUP")
	private String bloodGroup;
	
	@Column(name = "MOBILE_NO")
	private Long mobileNumber;
	
	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "ADDRESS")
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

	public String getUserName() {
		return userName;
	}

	public Long getUserId() {
		return userId;
	}

	public String getPassword() {
		return password;
	}

	public String getRole() {
		return role;
	}

	public String getGender() {
		return gender;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public Long getMobileNumber() {
		return mobileNumber;
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

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public void setMobileNumber(Long mobileNumber) {
		this.mobileNumber = mobileNumber;
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

}
