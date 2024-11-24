/**
 * 
 */
package com.laundry.LaundryManagement.repository;

import com.laundry.LaundryManagement.model.EstimationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author pandyarajan
 *
 * 12-Mar-2019
 */
@Repository
@Transactional(readOnly=false)
public interface EstimationRepository extends JpaRepository<EstimationDetails, Integer>{

	@Modifying
	@Query("update EstimationDetails est set est.billStatus=1 where est.estID = :estID")
	Integer updateEstimationStatus(@Param("estID") int estID);

	//EstimationDetails findById(int estID);

}
