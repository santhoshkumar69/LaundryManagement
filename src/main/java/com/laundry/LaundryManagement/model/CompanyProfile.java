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
@Table(name="TA_LA_COMPANY_PROFILE")
@Data
public class CompanyProfile  implements Serializable {
	
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name="COMPANY_NAME")
	private String companyName;
	
	//Add Logo;
	
	@Column(name="PERSON_NAME")
	private String personName;
	
	@Column(name="LOCATION")
	private String location;
	
	@Column(name="ADDRESS1")
	private String address1;
	
	@Column(name="ADDRESS2")
	private String address2;
	
	@Column(name="CITY")
	private String city;
	
	@Column(name="PHONE_No")
	private Integer phoneNo;
	
	@Column(name="MOBILE_No")
	private Integer MobileNo;
	
	@Column(name="FAX_No")
	private Integer faxNo;
	
	@Column(name="EMAIL_ID")
	private String emailId;
	
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

	public String getCompanyName() {
		return companyName;
	}

	public String getPersonName() {
		return personName;
	}

	public String getLocation() {
		return location;
	}

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public String getCity() {
		return city;
	}

	public Integer getPhoneNo() {
		return phoneNo;
	}

	public Integer getMobileNo() {
		return MobileNo;
	}

	public Integer getFaxNo() {
		return faxNo;
	}

	public String getEmailId() {
		return emailId;
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

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public void setLocation(String location) {
		location = location;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setPhoneNo(Integer phoneNo) {
		this.phoneNo = phoneNo;
	}

	public void setMobileNo(Integer mobileNo) {
		MobileNo = mobileNo;
	}

	public void setFaxNo(Integer faxNo) {
		this.faxNo = faxNo;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
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
