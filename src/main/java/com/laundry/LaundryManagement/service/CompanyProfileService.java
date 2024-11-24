package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.CompanyProfile;


public interface CompanyProfileService {
	
boolean companyCompanyProfile(CompanyProfile companyProfile);
	
	List<CompanyProfile> findCompanyProfile();
		
	CompanyProfile findByRow(Integer Id);
	
	Optional<CompanyProfile> findIdFromDb(Integer id);
	
	CompanyProfile findByCompanyProfileName(String companyProfileName);
	
	CompanyProfile findByIdss(Integer id);

}
