package com.laundry.LaundryManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.laundry.LaundryManagement.model.UserOutlet;

@Repository
public interface UserOutletRepo extends JpaRepository<UserOutlet, Integer> {

	List<UserOutlet> findByUserDbId(Integer userDbId);

	/** Runs immediately, so links re-added in the same transaction don't hit the unique (user, outlet) key. */
	@Modifying
	@Query("delete from UserOutlet u where u.userDbId = :userDbId")
	void deleteByUserDbId(@Param("userDbId") Integer userDbId);
}
