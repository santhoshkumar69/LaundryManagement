package com.laundry.LaundryManagement.service;

import java.util.List;

import com.laundry.LaundryManagement.model.PaymentType;

public interface PaymentTypeService {

	public boolean createPaymentType(PaymentType paymentType) throws Exception;
	
	public List<PaymentType> findAll();
	
	public List<PaymentType> findInactivePaymentType();	
	
	public List<PaymentType> activatePaymentType(Long[] ids);
	
	public List<PaymentType> deActivatePaymentType(Long[] ids);

}
