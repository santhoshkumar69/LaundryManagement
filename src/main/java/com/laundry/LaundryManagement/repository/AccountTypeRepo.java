package com.laundry.LaundryManagement.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.laundry.LaundryManagement.model.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
@Transactional
public interface AccountTypeRepo extends JpaRepository<AccountType, Integer>{
	
	@Query("From AccountType accountType where accountType.activeStatus=1")
	List<AccountType> findAllAccountType();
	
	@Query("From AccountType deacountType where deacountType.activeStatus=0")
	List<AccountType> findDeActiveAccountType();
	
	@Query("select accountType from AccountType accountType WHERE " + "accountType.id=:id")
	AccountType findByids(@Param("id") Integer id);
	
	@Query("from AccountType acounttype where acounttype.id=:id")
	Optional<AccountType> findByIdRow(@Param("id") Integer id);
	
	@Modifying
	@Query("update AccountType deactive set deactive.activeStatus=0 WHERE deactive.id=:id")
	Integer deActiveAccountType(@Param("id") Integer id);

}
