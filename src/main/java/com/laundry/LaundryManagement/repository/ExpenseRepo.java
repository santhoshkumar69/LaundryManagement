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

	/** Live expenses of the current outlet between two dates (inclusive); category null means all. */
	@Query("from Expense e where e.activeStatus = 1 and e.expenseDate between :from and :to"
			+ " and (:category is null or lower(e.category) = lower(:category)) order by e.expenseDate desc, e.id desc")
	List<Expense> findLive(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("category") String category);

	/** Id, category and subcategory of every live expense of the current outlet, oldest first. */
	@Query("select e.id, e.category, e.subcategory from Expense e where e.activeStatus = 1 order by e.id")
	List<Object[]> findCategoryPairs();
}
