package com.laundry.LaundryManagement.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.laundry.LaundryManagement.model.PaymentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.repository.PaymentTypeRepo;

@Service
public class PaymentTypeServiceImpl implements PaymentTypeService {

	@Autowired
	private PaymentTypeRepo paymentTypeRepo;

	@Override
	public boolean createPaymentType(PaymentType paymentType) throws Exception {
		try {
			paymentType.setActiveStatus(1);
			if (paymentType.getId() == null) {
				paymentType.setCreatedDate(new Date());
			} else {
				paymentType.setUpdatedDate(new Date());
			}
			paymentTypeRepo.save(paymentType);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public List<PaymentType> findAll() {
		return paymentTypeRepo.findAll();
	}

	@Override
	public List<PaymentType> activatePaymentType(Long[] ids) {
		List<PaymentType> thePaymentType = new ArrayList<PaymentType>();
		for(Long id : ids) {
			PaymentType paymentType = paymentTypeRepo.findById(id).orElse(null);
			if(paymentType != null) {
				paymentType.setActiveStatus(1);
				paymentTypeRepo.save(paymentType);
				thePaymentType.add(paymentType);
			}
		}
		return thePaymentType;
	}

	@Override
	public List<PaymentType> deActivatePaymentType(Long[] ids) {
		List<PaymentType> thePaymentType = new ArrayList<PaymentType>();
		for(Long id : ids) {
			PaymentType paymentType = paymentTypeRepo.findById(id).orElse(null);
			if(paymentType != null) {
				paymentType.setActiveStatus(0);
				paymentTypeRepo.save(paymentType);
				thePaymentType.add(paymentType);
			}
		}
		return thePaymentType;
	}

	@Override
	public List<PaymentType> findInactivePaymentType() {
		return paymentTypeRepo.findInactivePaymentType();
	}

	
}
