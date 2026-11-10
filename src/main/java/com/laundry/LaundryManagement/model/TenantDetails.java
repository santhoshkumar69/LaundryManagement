package com.laundry.LaundryManagement.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import com.laundry.LaundryManagement.auth.ScopeFilters;

import lombok.Data;

@Entity
@Data
@Table(name = "TB_LA_TENENT_DETAILS")
// Declared here once for the whole model; entities opt in with @Filter.
@FilterDef(name = ScopeFilters.TENANT, defaultCondition = ScopeFilters.TENANT_CONDITION, autoEnabled = true, applyToLoadByKey = true,
		parameters = @ParamDef(name = "tenantId", type = Integer.class, resolver = ScopeFilters.TenantIdResolver.class))
@FilterDef(name = ScopeFilters.OUTLET, defaultCondition = ScopeFilters.OUTLET_CONDITION, autoEnabled = true, applyToLoadByKey = true,
		parameters = @ParamDef(name = "outletId", type = String.class, resolver = ScopeFilters.OutletIdResolver.class))
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

	@Column(name = "CONTACT_NO")
	private String contactNo;

	@Column(name = "COUNTRY")
	private String country;

	/** PARTNER or DIRECT. */
	@Column(name = "CUSTOMER_TYPE")
	private String customerType;

	@Column(name = "ALT_CONTACT1_NAME")
	private String altContact1Name;

	@Column(name = "ALT_CONTACT1_NO")
	private String altContact1No;

	@Column(name = "ALT_CONTACT1_EMAIL")
	private String altContact1Email;

	@Column(name = "ALT_CONTACT2_NAME")
	private String altContact2Name;

	@Column(name = "ALT_CONTACT2_NO")
	private String altContact2No;

	@Column(name = "ALT_CONTACT2_EMAIL")
	private String altContact2Email;

	@Column(name = "ACTIVE_STATUS")
	private Integer activeStatus;

	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_DATE")
	private Date updatedDate;

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
