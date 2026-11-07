package com.laundry.LaundryManagement.service;

import com.laundry.LaundryManagement.dto.CompanyProfileDto;
import com.laundry.LaundryManagement.dto.OutletSettingsDto;
import com.laundry.LaundryManagement.dto.RegistrationRequest;
import com.laundry.LaundryManagement.dto.RegistrationResult;

/** Sign-up of tenants/outlets, and the company profile and outlet settings that come with them. */
public interface RegistrationService {

	RegistrationResult register(RegistrationRequest request);

	CompanyProfileDto getCompanyProfile(Long userId);

	CompanyProfileDto updateCompanyProfile(CompanyProfileDto profile);

	OutletSettingsDto getOutletSettings(Long userId);

	OutletSettingsDto updateOutletSettings(OutletSettingsDto settings);
}
