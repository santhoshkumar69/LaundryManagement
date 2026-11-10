package com.laundry.LaundryManagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.laundry.LaundryManagement.model.OutletSequence;

import jakarta.persistence.LockModeType;

@Repository
public interface OutletSequenceRepo extends JpaRepository<OutletSequence, Integer> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("from OutletSequence s where s.outletId = :outletId and s.series = :series")
	Optional<OutletSequence> findForUpdate(@Param("outletId") Integer outletId, @Param("series") String series);
}
