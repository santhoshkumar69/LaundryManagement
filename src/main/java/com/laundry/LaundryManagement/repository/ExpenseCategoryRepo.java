package com.laundry.LaundryManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.laundry.LaundryManagement.model.ExpenseCategory;

@Repository
public interface ExpenseCategoryRepo extends JpaRepository<ExpenseCategory, Long> {

	/** The current outlet's categories, active and deleted. */
	@Query("from ExpenseCategory c order by lower(c.name)")
	List<ExpenseCategory> findAllSorted();

	@Query("from ExpenseCategory c where lower(c.name) = lower(:name)")
	List<ExpenseCategory> findByNameIgnoreCase(@Param("name") String name);

	/** Keeps the name shown on expenses in step with a renamed category. */
	@Modifying
	@Query("update Expense e set e.category = :name where e.categoryId = :id")
	int renameOnExpenses(@Param("id") Long id, @Param("name") String name);
}
