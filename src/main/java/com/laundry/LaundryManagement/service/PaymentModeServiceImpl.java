package com.laundry.LaundryManagement.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.laundry.LaundryManagement.model.PaymentMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.repository.PaymentModeRepo;

@Service
public class PaymentModeServiceImpl implements PaymentModeService {
	
	@Autowired
	private PaymentModeRepo paymentModeRepo;

	@Override
	public boolean createPaymentMode(PaymentMode paymentMode) throws Exception {
		try {
			paymentMode.setActiveStatus(1);
			if (paymentMode.getId() == null) {
				paymentMode.setCreatedDate(new Date());
			} else {
				paymentMode.setUpdatedDate(new Date());
			}
			paymentModeRepo.save(paymentMode);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public List<PaymentMode> findAll() {
		return paymentModeRepo.findAll();
	}

	@Override
	public List<PaymentMode> activatePaymentMode(Long[] ids) {
		List<PaymentMode> thePaymentMode = new ArrayList<PaymentMode>();
		for(Long id : ids) {
			PaymentMode paymentMode = paymentModeRepo.findById(id).orElse(null);
			if(paymentMode != null) {
				paymentMode.setActiveStatus(1);
				paymentModeRepo.save(paymentMode);
				thePaymentMode.add(paymentMode);
			}
		}
		return thePaymentMode;
	}

	@Override
	public List<PaymentMode> deActivatePaymentMode(Long[] ids) {
		List<PaymentMode> thePaymentMode = new ArrayList<PaymentMode>();
		for(Long id : ids) {
			PaymentMode paymentMode = paymentModeRepo.findById(id).orElse(null);
			if(paymentMode != null) {
				paymentMode.setActiveStatus(0);
				paymentModeRepo.save(paymentMode);
				thePaymentMode.add(paymentMode);
			}
		}
		return thePaymentMode;
	}

	@Override
	public List<PaymentMode> findInactivePaymentMode() {
		return paymentModeRepo.findInactivePaymentMode();
	}

	
	
}
