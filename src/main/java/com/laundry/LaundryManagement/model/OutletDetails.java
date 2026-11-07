package com.laundry.LaundryManagement.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

	@Column(name = "CITY")
	private String city;

	@Column(name = "STATE")
	private String state;

	@Column(name = "COUNTRY")
	private String country;

	@Column(name = "POSTAL_CODE")
	private String postalCode;

	/** ISO 4217 code, e.g. QAR. */
	@Column(name = "CURRENCY")
	private String currency;

	@Column(name = "MOBILE_NO")
	private String mobileNo;

	@Column(name = "EMAIL_ID")
	private String emailId;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "REG_NO1")
	private String regNo1;

	@Column(name = "REG_NO2")
	private String regNo2;

	@Column(name = "LOGO_DATA", columnDefinition = "MEDIUMTEXT")
	private String logoData;

	@Column(name = "DATE_FORMAT")
	private String dateFormat;

	@Column(name = "TIME_FORMAT")
	private String timeFormat;

	@Column(name = "TIME_ZONE")
	private String timeZone;

	/** NONE, 0.05, 0.10, 0.25, 0.50 or 1.00. */
	@Column(name = "ROUND_OFF")
	private String roundOff;

	@Column(name = "DECIMAL_SYMBOL")
	private String decimalSymbol;

	/** Month (1-12) the financial year starts in. */
	@Column(name = "FINANCIAL_YEAR_START")
	private Integer financialYearStart;

	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_DATE")
	private Date updatedDate;

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
