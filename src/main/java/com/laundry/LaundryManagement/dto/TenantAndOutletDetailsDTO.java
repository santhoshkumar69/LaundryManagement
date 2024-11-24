package com.laundry.LaundryManagement.dto;

import java.util.List;

public class TenantAndOutletDetailsDTO {

	private Integer id;
	
	private String companyName;
	
	private String contactName;

	private String emailId;

	private String address;

	private Integer noOfOutlets;

	private String subscription;

	private String tenantCode;

	private List<OutletDetailsDTO> theOutLetDetailsDTO;

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

	public List<OutletDetailsDTO> getTheOutLetDetailsDTO() {
		return theOutLetDetailsDTO;
	}

	public void setTheOutLetDetailsDTO(List<OutletDetailsDTO> theOutLetDetailsDTO) {
		this.theOutLetDetailsDTO = theOutLetDetailsDTO;
	}
	
	
	
}
