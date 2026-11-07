package com.laundry.LaundryManagement.dto;

import lombok.Data;

/**
 * Public sign-up. A new customer creates a tenant, its first outlet and an admin login;
 * an existing customer (existingCustomer = true) adds an outlet and admin login to its tenant
 * after proving it with an existing admin's login.
 */
@Data
public class RegistrationRequest {

	private boolean existingCustomer;

	/** Existing customer only. */
	private String tenantCode;
	private Long verifyUserId;
	private String verifyPassword;

	/** New customer only. */
	private String companyName;
	private String customerType;
	private String subscription;
	private String altContact1Name;
	private String altContact1No;
	private String altContact1Email;
	private String altContact2Name;
	private String altContact2No;
	private String altContact2Email;

	private String outletName;
	private String city;
	private String state;
	private String country;
	private String postalCode;
	private String currency;

	private String contactName;
	private String contactNo;
	private String emailId;

	private Long loginId;
	private String password;
}
