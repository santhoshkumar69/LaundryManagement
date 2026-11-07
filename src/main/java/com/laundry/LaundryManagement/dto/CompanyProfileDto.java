package com.laundry.LaundryManagement.dto;

import lombok.Data;

@Data
public class CompanyProfileDto {
	/** Logged-in user; the profile shown and saved is that user's tenant. */
	private Long userId;

	private String tenantCode;
	private String companyName;
	private String emailId;
	private String contactName;
	private String contactNo;
	private String country;
}
