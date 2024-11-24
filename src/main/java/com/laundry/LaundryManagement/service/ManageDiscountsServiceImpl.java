package com.laundry.LaundryManagement.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.laundry.LaundryManagement.model.Discounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.repository.ManageDiscountsRepo;

@Service
public class ManageDiscountsServiceImpl implements ManageDiscountsService {
		
		@Autowired
		private ManageDiscountsRepo manageDiscountsRepo;

		@Override
		public boolean createDiscounts(Discounts discounts) throws Exception {
			try {
				discounts.setActiveStatus(1);
				if (discounts.getId() == null) {
					discounts.setCreatedDate(new Date());
				} else {
					discounts.setUpdatedDate(new Date());
				}
				manageDiscountsRepo.save(discounts);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}

		@Override
		public List<Discounts> findAll() {
			return manageDiscountsRepo.findAll();
		}

		

		@Override
		public List<Discounts> deActivateDiscounts(Long[] ids) {
			List<Discounts> theDiscounts = new ArrayList<Discounts>();
			for(Long id : ids) {
				Discounts discounts = manageDiscountsRepo.findById(id).orElse(null);
				if(discounts != null) {
					discounts.setActiveStatus(0);
					manageDiscountsRepo.save(discounts);
				}
			}
			theDiscounts = manageDiscountsRepo.findActiveDiscounts();
			return theDiscounts;
		}

		@Override
		public List<Discounts> findInactiveDiscounts() {
			return manageDiscountsRepo.findInactiveDiscounts();
		}

		

		@Override
		public List<Discounts> activateDiscounts(Long[] ids) {
			List<Discounts> theDiscounts = new ArrayList<Discounts>();
			for(Long id : ids) {
				Discounts discounts = manageDiscountsRepo.findById(id).orElse(null);
				if(discounts != null) {
					discounts.setActiveStatus(1);
					manageDiscountsRepo.save(discounts);
					theDiscounts.add(discounts);
				}
			}
			return theDiscounts;
		}
		
		@Override
		public List<Discounts> findActiveDiscounts() {
			return manageDiscountsRepo.findActiveDiscounts();
		}
	}


