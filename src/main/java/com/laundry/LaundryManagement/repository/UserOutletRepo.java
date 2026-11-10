package com.laundry.LaundryManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.laundry.LaundryManagement.model.UserOutlet;

@Repository
public interface UserOutletRepo extends JpaRepository<UserOutlet, Integer> {

	List<UserOutlet> findByUserDbId(Integer userDbId);

	void deleteByUserDbId(Integer userDbId);
}
