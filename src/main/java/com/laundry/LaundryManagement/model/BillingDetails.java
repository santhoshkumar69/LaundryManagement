package com.laundry.LaundryManagement.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "TB_BILLING_DETAILS")
@Data
public class BillingDetails extends BaseEntity implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int Id;
	
	@Column(name = "ITEM_CATEGORY_ID")
	private int itemCategoryId;
	
	@Column(name = "ITEM_CODE")
	private int itemCode;
	
	@Column(name = "ITEM_NAME")
	private String itemName;
	
	@Column(name = "ARABIC_NAME")
	private String arabicName;
	
	@Column(name = "ACTIVE_STATUS")
	private int activeStatus;
	
	@Column(name = "USER_ID")
	private int userId;
	
	@Column(name = "FREQUENTLY_USED")
	private boolean frequentlyUsed;
	
	@OneToMany(mappedBy = "billingDetails", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<CartDetails> theCartDetails;
	
	@OneToMany(mappedBy = "billingDetails", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<ExtraCharges> theExtraCharges;
	
	@OneToMany(mappedBy = "billingDetails", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Discounts> theDiscounts;
	
	@Column(name = "TENANT_ID")
	private int tenantId;
	
	/**
	 * @return the id
	 */
	public int getId() {
		return Id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		Id = id;
	}

	/**
	 * @return the itemCategoryId
	 */
	public int getItemCategoryId() {
		return itemCategoryId;
	}

	/**
	 * @param itemCategoryId the itemCategoryId to set
	 */
	public void setItemCategoryId(int itemCategoryId) {
		this.itemCategoryId = itemCategoryId;
	}

	/**
	 * @return the itemCode
	 */
	public int getItemCode() {
		return itemCode;
	}

	/**
	 * @param itemCode the itemCode to set
	 */
	public void setItemCode(int itemCode) {
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
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
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

	/**
	 * @return the theCartDetails
	 */
	public List<CartDetails> getTheCartDetails() {
		return theCartDetails;
	}

	/**
	 * @param theCartDetails the theCartDetails to set
	 */
	public void setTheCartDetails(List<CartDetails> theCartDetails) {
		this.theCartDetails = theCartDetails;
	}

	/**
	 * @return the theExtraCharges
	 */
	public List<ExtraCharges> getTheExtraCharges() {
		return theExtraCharges;
	}

	/**
	 * @param theExtraCharges the theExtraCharges to set
	 */
	public void setTheExtraCharges(List<ExtraCharges> theExtraCharges) {
		this.theExtraCharges = theExtraCharges;
	}

	/**
	 * @return the theDiscounts
	 */
	public List<Discounts> getTheDiscounts() {
		return theDiscounts;
	}

	/**
	 * @param theDiscounts the theDiscounts to set
	 */
	public void setTheDiscounts(List<Discounts> theDiscounts) {
		this.theDiscounts = theDiscounts;
	}
	
}
