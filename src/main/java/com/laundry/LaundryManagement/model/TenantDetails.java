package com.laundry.LaundryManagement.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "TB_LA_TENENT_DETAILS")
public class TenantDetails implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private Integer id;
	
	@Column(name = "COMPANY_NAME")
	private String companyName;
	
	@Column(name = "CONTACT_NAME")
	private String contactName;
	
	@Column(name = "EMAIL_ID")
	private String emailId;
	
	@Column(name = "ADDRESS")
	private String address;
	
	@Column(name = "NO_OF_OUTLETS")
	private Integer noOfOutlets;
	
	@Column(name = "SUBSCRIPTION")
	private String subscription;
	
	@Column(name = "TENANT_CODE")
	private String tenantCode;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getNoOfOutlets() {
		return noOfOutlets;
	}

	public void setNoOfOutlets(Integer noOfOutlets) {
		this.noOfOutlets = noOfOutlets;
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}
	
	
}
