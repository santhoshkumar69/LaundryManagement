package com.laundry.LaundryManagement.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="TA_LA_EXTRA_CHARGES")
@Data
public class ExtraCharges implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private Long id;
	
	@Column(name="ACTIVE_STATUS" ,nullable=false)
	private int activeStatus;
	
	@Column(name="CHARGE_NAME")
	private String chargeName;
	
	@Column(name="CHARGE_TYPE")
	private String chargeType;
	
	@Column(name="CHARGE_VALUE")
	private int chargeValue;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_DATE")
	private Date updatedDate;
	
	@Column(name = "IS_OPEN")
	private boolean isOpen;
	
	@ManyToOne
	@JoinColumn(name = "billing_details_id")
	private BillingDetails billingDetails;
	
	@Column(name = "TENANT_ID")
	private int tenantId;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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
	 * @return the chargeName
	 */
	public String getChargeName() {
		return chargeName;
	}

	/**
	 * @param chargeName the chargeName to set
	 */
	public void setChargeName(String chargeName) {
		this.chargeName = chargeName;
	}

	/**
	 * @return the chargeType
	 */
	public String getChargeType() {
		return chargeType;
	}

	/**
	 * @param chargeType the chargeType to set
	 */
	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}

	/**
	 * @return the chargeValue
	 */
	public int getChargeValue() {
		return chargeValue;
	}

	/**
	 * @param chargeValue the chargeValue to set
	 */
	public void setChargeValue(int chargeValue) {
		this.chargeValue = chargeValue;
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
	 * @return the billingDetails
	 */
	public BillingDetails getBillingDetails() {
		return billingDetails;
	}

	/**
	 * @param billingDetails the billingDetails to set
	 */
	public void setBillingDetails(BillingDetails billingDetails) {
		this.billingDetails = billingDetails;
	}

	/**
	 * @return the isOpen
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * @param isOpen the isOpen to set
	 */
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	
}
