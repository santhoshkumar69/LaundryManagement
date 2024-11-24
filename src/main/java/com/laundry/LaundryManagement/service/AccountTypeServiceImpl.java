package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.laundry.LaundryManagement.model.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.repository.AccountTypeRepo;

@Service
public class AccountTypeServiceImpl implements AccountTypeService{

	@Autowired
	AccountTypeRepo accountTypeRepo;
	
	@Override
	@Transactional
	public boolean createAccountType(AccountType accountType) {
		
		try {
			accountTypeRepo.save(accountType);
			return true;
		}catch (Exception Ex){
       Ex.printStackTrace();
       return false;
		}}

	@Override
	@Transactional
	public List<AccountType> findAllAccountType() {
		
		return accountTypeRepo.findAllAccountType();
	}

	@Override
	public List<AccountType> findDeActiveAccountType() {
	
		return accountTypeRepo.findDeActiveAccountType();
	}

	@Override
	public AccountType findByRow(Integer Id) {
	
		return accountTypeRepo.findByids(Id);
	}

	@Override
	public Optional<AccountType> findIdFromDb(Integer id) {
		
		return accountTypeRepo.findByIdRow(id);
	}

}
