package com.laundry.LaundryManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationResult {
	private String tenantCode;
	private String companyName;
	private String outletCode;
	private String outletName;
	private Long loginId;
}
