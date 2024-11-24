package com.laundry.LaundryManagement.service;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.AccountType;

public interface AccountTypeService {
	
	boolean createAccountType(AccountType accountType);
	
	List<AccountType> findAllAccountType();
	
	List<AccountType> findDeActiveAccountType();

	AccountType findByRow(Integer Id);
	
	Optional<AccountType> findIdFromDb(Integer id);
}
