package com.laundry.LaundryManagement.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.laundry.LaundryManagement.auth.RequestScope;
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
			String user = RequestScope.require().userName();
			PaymentMode target;
			if (paymentMode.getId() == null) {
				target = new PaymentMode();
				target.setActiveStatus(1);
				target.setCreatedBy(user);
				target.setCreatedDate(new Date());
			} else {
				target = paymentModeRepo.findById(paymentMode.getId()).orElse(null);
				if (target == null) {
					return false;
				}
				target.setUpdatedBy(user);
				target.setUpdatedDate(new Date());
			}
			target.setPaymentMode(paymentMode.getPaymentMode().trim());
			target.setNeedsCardType(paymentMode.isNeedsCardType());
			paymentModeRepo.save(target);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/** Active payment modes of the current outlet. */
	@Override
	public List<PaymentMode> findAll() {
		return paymentModeRepo.findActivePaymentMode();
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
