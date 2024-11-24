package com.laundry.LaundryManagement.repository;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by rajeevkumarsingh on 27/06/17.
 */

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

	@Query("select userInfo from UserInfo userInfo where "
			+ "userInfo.userId = :userId and userInfo.password = :password ")
	public UserInfo findByLaundryUserId(@Param("userId") Long userId, @Param("password") String password);
	
	@Override
	//@Query("From UserInfo c where c.activeStatus=1")
	@Query("From UserInfo userInfo where userInfo.activeStatus=1")
	List<UserInfo> findAll();
	
	@Modifying
	@Query("update UserInfo c set c.activeStatus=0 WHERE c.id=:userId")
	Integer deactivateUserInfo(@Param("userId") Integer userId);
	
	@Query("from UserInfo userInfo where userInfo.id=id")
	List<UserInfo> findById();
	
	@Query(value = "SELECT user_name FROM tb_la_user_info;",nativeQuery = true)
	public List<UserInfo> getMobileNoData();
	

	
	
	@Query("select userInfo from UserInfo userInfo where "
			+ "userInfo.userId = :userId ")
	UserInfo findByUserId(@Param("userId") Long userId);
	
	@Modifying
	@Query("update UserInfo c set c.activeStatus=1 WHERE c.id=:userId")
	Integer activateUserInfo(@Param("userId") Integer userId);
	
	
	@Query("From UserInfo userInfo where userInfo.activeStatus=0")
	List<UserInfo> getAllDeActiveUser();

	public Optional<UserInfo> findById(Integer id);
}
