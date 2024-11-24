package com.laundry.LaundryManagement.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "TB_LA_OUTLET_DETAILS")
public class OutletDetails implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name= "ID")
	private Integer id;
	
	@Column(name = "OUTLET_NAME")
	private String outletName;
	
	@Column(name = "TENANT_ID")
	private int tenantId;
	
	@Column(name = "OUTLET_CODE")
	private String outletCode;

	public String getOutletName() {
		return outletName;
	}

	public void setOutletName(String outletName) {
		this.outletName = outletName;
	}

	public int getTenantId() {
		return tenantId;
	}

	public void setTenantId(int tenantId) {
		this.tenantId = tenantId;
	}

	public String getOutletCode() {
		return outletCode;
	}

	public void setOutletCode(String outletCode) {
		this.outletCode = outletCode;
	}
	
	
}
