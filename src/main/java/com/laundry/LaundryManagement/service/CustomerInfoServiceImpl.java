package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.laundry.LaundryManagement.model.CustomerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.repository.CustomerInfoRepo;

@Service
public class CustomerInfoServiceImpl implements CustomerInfoService {

	@Autowired
	CustomerInfoRepo customerInfoRepo;
	
	@Override
	@Transactional
	public boolean createCustomerInfo(CustomerInfo customerInfo) {
		
		try {
			//customerInfo.setActiveStatus(1);
			customerInfoRepo.save(customerInfo);
			return true;
		}catch (Exception Ex){
       Ex.printStackTrace();
       return false;
		}}

	@Override
	@Transactional
	public List<CustomerInfo> fetchCustomerInfoDetails() {
		
		return customerInfoRepo.findAll();
	}

	@Override
	@Transactional
	public void deactivateCustomerInfo(Integer userId) {
		
		 customerInfoRepo.deactivateCustomerInfo(userId);
		
	}

	@Override
	@Transactional
	public Optional<CustomerInfo> findOne(Integer id) {
	
		return customerInfoRepo.findById(id);
	}

	@Override
	@Transactional
	public CustomerInfo findByQatarId(Long qatarId) {
		return customerInfoRepo.findByQatarId(qatarId);
	}

	@Override
	public CustomerInfo findByMobileNo(String mobileNo) {
		
		return customerInfoRepo.findByMobNo(mobileNo);
	}

	@Override
	public CustomerInfo findDeleteId(Integer id) {
		
		return customerInfoRepo.findByIdDelete(id);
	}

	@Override
	public List<CustomerInfo> deActiveFetchCustomer() {
		
		return customerInfoRepo.deactiveRecords();
	}

	
}
