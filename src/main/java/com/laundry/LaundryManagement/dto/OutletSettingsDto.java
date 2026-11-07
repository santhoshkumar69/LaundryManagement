package com.laundry.LaundryManagement.dto;

import lombok.Data;

@Data
public class OutletSettingsDto {
	/** Logged-in user; the settings shown and saved are for that user's outlet. */
	private Long userId;

	private String outletCode;
	private String outletName;

	private String dateFormat;
	private String timeFormat;
	private String timeZone;
	private String roundOff;
	private String decimalSymbol;
	private Integer financialYearStart;

	private String postalCode;
	private String city;
	private String state;
	private String country;
	private String currency;
	private String mobileNo;
	private String emailId;
	private String address;
	private String regNo1;
	private String regNo2;
	private String logoData;
}
