package com.laundry.LaundryManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.laundry.LaundryManagement.model.ExpenseSubcategory;

@Repository
public interface ExpenseSubcategoryRepo extends JpaRepository<ExpenseSubcategory, Long> {

	/** The current outlet's subcategories, active and deleted. */
	@Query("from ExpenseSubcategory s order by lower(s.name)")
	List<ExpenseSubcategory> findAllSorted();

	@Query("from ExpenseSubcategory s where s.categoryId = :categoryId and lower(s.name) = lower(:name)")
	List<ExpenseSubcategory> findByNameIgnoreCase(@Param("categoryId") Long categoryId, @Param("name") String name);

	/** Keeps the name shown on expenses in step with a renamed subcategory. */
	@Modifying
	@Query("update Expense e set e.subcategory = :name where e.subcategoryId = :id")
	int renameOnExpenses(@Param("id") Long id, @Param("name") String name);
}
