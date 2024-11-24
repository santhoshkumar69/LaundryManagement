package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.laundry.LaundryManagement.model.OutGoingPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.repository.OutGoingPaymentRepo;

@Service
public class OutGoingPaymentServiceImpl implements OutGoingPaymentService{

	@Autowired
	OutGoingPaymentRepo outGoingPaymentRepo;
	
	
	@Override
	@Transactional
	public boolean createOutGoingPayment(OutGoingPayment outGoingPayment) {
		
		try {
			outGoingPaymentRepo.save(outGoingPayment);
			return true;
		}catch (Exception Ex) {
			Ex.printStackTrace();
			return false;
		}
		
	}

	@Override
	@Transactional
	public List<OutGoingPayment> fetchOutGoingPayment() {
		
		return outGoingPaymentRepo.findAllOutGoingpayment();
	}

	@Override
	@Transactional
	public Optional<OutGoingPayment> findOne(Integer id) {
		
		return outGoingPaymentRepo.findByIdRow(id);
	}

	@Override
	@Transactional
	public OutGoingPayment findById(Integer id)
	{		
		return outGoingPaymentRepo.findByids(id);
	}

	
	
}
