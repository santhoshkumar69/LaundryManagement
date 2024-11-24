package com.laundry.LaundryManagement.service;

import java.util.List;

import com.laundry.LaundryManagement.model.PaymentMode;

public interface PaymentModeService {

	public boolean createPaymentMode(PaymentMode paymentMode) throws Exception;
	
	public List<PaymentMode> findAll();
	
	public List<PaymentMode> findInactivePaymentMode();
	
	public List<PaymentMode> activatePaymentMode(Long[] ids);
	
	public List<PaymentMode> deActivatePaymentMode(Long[] ids);

	
}
