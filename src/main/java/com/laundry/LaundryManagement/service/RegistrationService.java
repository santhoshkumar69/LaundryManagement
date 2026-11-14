package com.laundry.LaundryManagement.service;

import com.laundry.LaundryManagement.dto.CompanyProfileDto;
import com.laundry.LaundryManagement.dto.OutletSettingsDto;
import com.laundry.LaundryManagement.dto.RegistrationRequest;
import com.laundry.LaundryManagement.dto.RegistrationResult;

/** Sign-up of tenants/outlets, and the company profile and outlet settings that come with them. */
public interface RegistrationService {

	RegistrationResult register(RegistrationRequest request);

	CompanyProfileDto getCompanyProfile(String userId);

	CompanyProfileDto updateCompanyProfile(CompanyProfileDto profile);

	OutletSettingsDto getOutletSettings(String userId, Integer outletId);

	OutletSettingsDto updateOutletSettings(OutletSettingsDto settings);
}
