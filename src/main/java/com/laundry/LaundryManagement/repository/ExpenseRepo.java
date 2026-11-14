package com.laundry.LaundryManagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.laundry.LaundryManagement.model.Expense;

@Repository
public interface ExpenseRepo extends JpaRepository<Expense, Long> {

	/** Live expenses of the current outlet between two dates (inclusive); null filters mean all. */
	@Query("from Expense e where e.activeStatus = 1 and e.expenseDate between :from and :to"
			+ " and (:categoryId is null or e.categoryId = :categoryId)"
			+ " and (:category is null or lower(e.category) = lower(:category)) order by e.expenseDate desc, e.id desc")
	List<Expense> findLive(@Param("from") LocalDate from, @Param("to") LocalDate to,
			@Param("categoryId") Long categoryId, @Param("category") String category);

	/** Number of live expenses per category of the current outlet. */
	@Query("select e.categoryId, count(e) from Expense e where e.activeStatus = 1 and e.categoryId is not null"
			+ " group by e.categoryId")
	List<Object[]> countByCategory();

	/** Number of live expenses per subcategory of the current outlet. */
	@Query("select e.subcategoryId, count(e) from Expense e where e.activeStatus = 1 and e.subcategoryId is not null"
			+ " group by e.subcategoryId")
	List<Object[]> countBySubcategory();
}
