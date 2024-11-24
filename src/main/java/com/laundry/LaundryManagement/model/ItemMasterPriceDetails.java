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
@Table(name = "TB_LA_ITEM_MASTER_PRICE_DETAILS")
public class ItemMasterPriceDetails implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer ID;
	
	@Column(name = "WASHING_TYPE_ID")
	private int washingTypeId;
	
	@Column(name = "SERVE_UNIT_ID")
	private int unitId;
	
	@Column(name = "ORDINARY_PRICE", nullable = false)
	private int ordinaryPrice;
	
	@Column(name = "URGENT_PRICE", nullable = true)
	private int urgentPrice;
	
	@Column(name = "ITEM_MASTER_ID", nullable = false)
	private int itemMasterId;
	
	@Column(name = "TENANT_ID")
	private int tenantId;
	

	/**
	 * @return the iD
	 */
	public Integer getID() {
		return ID;
	}

	/**
	 * @param iD the iD to set
	 */
	public void setID(Integer iD) {
		ID = iD;
	}

	/**
	 * @return the ordinaryPrice
	 */
	public int getOrdinaryPrice() {
		return ordinaryPrice;
	}

	/**
	 * @param ordinaryPrice the ordinaryPrice to set
	 */
	public void setOrdinaryPrice(int ordinaryPrice) {
		this.ordinaryPrice = ordinaryPrice;
	}

	/**
	 * @return the urgentPrice
	 */
	public int getUrgentPrice() {
		return urgentPrice;
	}

	/**
	 * @param urgentPrice the urgentPrice to set
	 */
	public void setUrgentPrice(int urgentPrice) {
		this.urgentPrice = urgentPrice;
	}

	/**
	 * @return the washingTypeId
	 */
	public int getWashingTypeId() {
		return washingTypeId;
	}

	/**
	 * @param washingTypeId the washingTypeId to set
	 */
	public void setWashingTypeId(int washingTypeId) {
		this.washingTypeId = washingTypeId;
	}

	/**
	 * @return the unitId
	 */
	public int getUnitId() {
		return unitId;
	}

	/**
	 * @param unitId the unitId to set
	 */
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}

	/**
	 * @return the itemMasterId
	 */
	public int getItemMasterId() {
		return itemMasterId;
	}

	/**
	 * @param itemMasterId the itemMasterId to set
	 */
	public void setItemMasterId(int itemMasterId) {
		this.itemMasterId = itemMasterId;
	}
	
}
