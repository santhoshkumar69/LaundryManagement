package com.laundry.LaundryManagement.repository;

import java.util.List;
import java.util.Optional;

import com.laundry.LaundryManagement.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = false)
public interface UnitRepo extends JpaRepository<Unit, Long>{
	
	@Override
	@Query("From Unit unit where unit.activeStatus=1")
	List<Unit> findAll();
	
	@Modifying
	@Query("update Unit unit set unit.activeStatus=0 WHERE unit.id=:id")
	Integer deactivateUnit(@Param("id") Integer id);
	
	@Query("from Unit unit where unit.id = :id ")
	Unit findByUnit(@Param("id") Integer id);
	
	@Query("select unit from Unit unit where "
			+ "unit.unit = :unit ")
	Unit findUnit(@Param("unit") String unit);


	@Query("from Unit unit where unit.id = :id ")
	Optional<Unit> findByIds(@Param("id") Integer id);
	
	@Query("From Unit unit where unit.activeStatus=0")
	List<Unit> getAllDeActiveUnit();

}
