package com.laundry.LaundryManagement.model;

import org.hibernate.annotations.Filter;
import jakarta.persistence.EntityListeners;
import com.laundry.LaundryManagement.auth.ScopeFilters;
import com.laundry.LaundryManagement.auth.ScopeStampingListener;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;


@Entity
@Table(name="TB_CART_DETAILS")
@Data
@Filter(name = ScopeFilters.TENANT)
@Filter(name = ScopeFilters.OUTLET)
@EntityListeners(ScopeStampingListener.class)
public class CartDetails extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int Id;

	@Column(name = "LOCATION_ID")
	private String locationID;
	
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
