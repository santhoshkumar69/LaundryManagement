package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.OutGoingPayment;

public interface OutGoingPaymentService {

	boolean createOutGoingPayment(OutGoingPayment outGoingPayment);
	
	List<OutGoingPayment> fetchOutGoingPayment();
	
	Optional<OutGoingPayment> findOne(Integer id);
	
	OutGoingPayment findById(Integer id);
 }
