package com.laundry.LaundryManagement.model;

import java.io.Serializable;

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
@Table(name="TB_CART_DETAILS")
@Data
public class CartDetails extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int Id;
	
	@ManyToOne
	@JoinColumn(name = "billing_details_id")
	private BillingDetails billingDetails;
	
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
	
	
}
