package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.CustomerInfo;


public interface CustomerInfoService {
	
	boolean createCustomerInfo(CustomerInfo customerInfo);

	List<CustomerInfo> fetchCustomerInfoDetails();
	
	void deactivateCustomerInfo(Integer userId);
    
	Optional<CustomerInfo> findOne(Integer id);
	
	CustomerInfo findByQatarId(Long qatarId);
	
	CustomerInfo findByMobileNo(String mobileNo);
	
	CustomerInfo findDeleteId(Integer id);
	
	List<CustomerInfo> deActiveFetchCustomer();
	
}
