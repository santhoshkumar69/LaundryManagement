package com.laundry.LaundryManagement.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.model.ExpenseCategory;
import com.laundry.LaundryManagement.model.ExpenseSubcategory;
import com.laundry.LaundryManagement.repository.ExpenseCategoryRepo;
import com.laundry.LaundryManagement.repository.ExpenseRepo;
import com.laundry.LaundryManagement.repository.ExpenseSubcategoryRepo;

/**
 * Accounts & Reports > Expense Categories: the outlet's categories and the subcategories under each. Deleting only
 * hides an entry from new expenses; expenses already recorded keep showing its name.
 */
@Service
public class ExpenseCategoryService {

	private static final int MAX_NAME_LENGTH = 100;

	public record SubcategoryView(Long id, Long categoryId, String name, boolean active, long usedBy) {
	}

	public record CategoryView(Long id, String name, boolean active, long usedBy, List<SubcategoryView> subcategories) {
	}

	public record SaveCategoryRequest(Long id, String name) {
	}

	public record SaveSubcategoryRequest(Long id, Long categoryId, String name) {
	}

	@Autowired
	private ExpenseCategoryRepo categoryRepo;

	@Autowired
	private ExpenseSubcategoryRepo subcategoryRepo;

	@Autowired
	private ExpenseRepo expenseRepo;

	/** The outlet's categories with their subcategories; deleted ones only when asked for. */
	public List<CategoryView> list(boolean includeDeleted) {
		Map<Long, Long> categoryUse = counts(expenseRepo.countByCategory());
		Map<Long, Long> subcategoryUse = counts(expenseRepo.countBySubcategory());
		List<ExpenseSubcategory> subcategories = subcategoryRepo.findAllSorted();
		return categoryRepo.findAllSorted().stream()
				.filter(c -> includeDeleted || c.getActiveStatus() == 1)
				.map(c -> new CategoryView(c.getId(), c.getName(), c.getActiveStatus() == 1,
						categoryUse.getOrDefault(c.getId(), 0L),
						subcategories.stream()
								.filter(s -> Objects.equals(s.getCategoryId(), c.getId())
										&& (includeDeleted || s.getActiveStatus() == 1))
								.map(s -> new SubcategoryView(s.getId(), s.getCategoryId(), s.getName(),
										s.getActiveStatus() == 1, subcategoryUse.getOrDefault(s.getId(), 0L)))
								.toList()))
				.toList();
	}

	@Transactional
	public CategoryView saveCategory(SaveCategoryRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Category details are required");
		}
		String name = name(request.name(), "Category name");
		for (ExpenseCategory other : categoryRepo.findByNameIgnoreCase(name)) {
			if (!Objects.equals(other.getId(), request.id())) {
				throw new IllegalArgumentException(other.getActiveStatus() == 1
						? "A category named \"" + other.getName() + "\" already exists"
						: "A deleted category named \"" + other.getName() + "\" exists. Restore it instead.");
			}
		}
		String user = RequestScope.require().userName();
		ExpenseCategory category;
		if (request.id() == null) {
			category = new ExpenseCategory();
			category.setCreatedBy(user);
			category.setCreatedDate(new Date());
		} else {
			category = category(request.id());
			category.setUpdatedBy(user);
			category.setUpdatedDate(new Date());
		}
		category.setName(name);
		category = categoryRepo.saveAndFlush(category);
		if (request.id() != null) {
			categoryRepo.renameOnExpenses(category.getId(), name);
		}
		return view(category);
	}

	@Transactional
	public void deleteCategory(long id) {
		ExpenseCategory category = category(id);
		if (category.getActiveStatus() != 1) {
			throw new IllegalArgumentException("Category is already deleted");
		}
		setStatus(category, 0);
	}

	@Transactional
	public void restoreCategory(long id) {
		ExpenseCategory category = category(id);
		if (category.getActiveStatus() == 1) {
			throw new IllegalArgumentException("Category is not deleted");
		}
		setStatus(category, 1);
	}

	@Transactional
	public SubcategoryView saveSubcategory(SaveSubcategoryRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Subcategory details are required");
		}
		ExpenseSubcategory subcategory = request.id() == null ? new ExpenseSubcategory() : subcategory(request.id());
		Long categoryId = request.id() == null ? request.categoryId() : subcategory.getCategoryId();
		if (categoryId == null) {
			throw new IllegalArgumentException("Category is required");
		}
		ExpenseCategory category = category(categoryId);
		if (request.id() == null && category.getActiveStatus() != 1) {
			throw new IllegalArgumentException("Restore the category \"" + category.getName() + "\" first");
		}
		String name = name(request.name(), "Subcategory name");
		for (ExpenseSubcategory other : subcategoryRepo.findByNameIgnoreCase(categoryId, name)) {
			if (!Objects.equals(other.getId(), request.id())) {
				throw new IllegalArgumentException(other.getActiveStatus() == 1
						? "\"" + category.getName() + "\" already has a subcategory named \"" + other.getName() + "\""
						: "A deleted subcategory named \"" + other.getName() + "\" exists under \"" + category.getName()
								+ "\". Restore it instead.");
			}
		}
		String user = RequestScope.require().userName();
		if (request.id() == null) {
			subcategory.setCategoryId(categoryId);
			subcategory.setCreatedBy(user);
			subcategory.setCreatedDate(new Date());
		} else {
			subcategory.setUpdatedBy(user);
			subcategory.setUpdatedDate(new Date());
		}
		subcategory.setName(name);
		subcategory = subcategoryRepo.saveAndFlush(subcategory);
		if (request.id() != null) {
			subcategoryRepo.renameOnExpenses(subcategory.getId(), name);
		}
		return new SubcategoryView(subcategory.getId(), categoryId, name, subcategory.getActiveStatus() == 1,
				counts(expenseRepo.countBySubcategory()).getOrDefault(subcategory.getId(), 0L));
	}

	@Transactional
	public void deleteSubcategory(long id) {
		ExpenseSubcategory subcategory = subcategory(id);
		if (subcategory.getActiveStatus() != 1) {
			throw new IllegalArgumentException("Subcategory is already deleted");
		}
		setStatus(subcategory, 0);
	}

	@Transactional
	public void restoreSubcategory(long id) {
		ExpenseSubcategory subcategory = subcategory(id);
		if (subcategory.getActiveStatus() == 1) {
			throw new IllegalArgumentException("Subcategory is not deleted");
		}
		ExpenseCategory category = category(subcategory.getCategoryId());
		if (category.getActiveStatus() != 1) {
			throw new IllegalArgumentException("Restore the category \"" + category.getName() + "\" first");
		}
		setStatus(subcategory, 1);
	}

	/** A category of the current outlet (other outlets' rows are filtered out), active or deleted. */
	public ExpenseCategory category(long id) {
		ExpenseCategory category = categoryRepo.findById(id).orElse(null);
		if (category == null) {
			throw new IllegalArgumentException("Category not found");
		}
		return category;
	}

	/** A subcategory of the current outlet, active or deleted. */
	public ExpenseSubcategory subcategory(long id) {
		ExpenseSubcategory subcategory = subcategoryRepo.findById(id).orElse(null);
		if (subcategory == null) {
			throw new IllegalArgumentException("Subcategory not found");
		}
		return subcategory;
	}

	private void setStatus(ExpenseCategory category, int status) {
		category.setActiveStatus(status);
		category.setUpdatedBy(RequestScope.require().userName());
		category.setUpdatedDate(new Date());
		categoryRepo.save(category);
	}

	private void setStatus(ExpenseSubcategory subcategory, int status) {
		subcategory.setActiveStatus(status);
		subcategory.setUpdatedBy(RequestScope.require().userName());
		subcategory.setUpdatedDate(new Date());
		subcategoryRepo.save(subcategory);
	}

	private CategoryView view(ExpenseCategory category) {
		return list(true).stream().filter(c -> c.id().equals(category.getId())).findFirst().orElseThrow();
	}

	private static Map<Long, Long> counts(List<Object[]> rows) {
		Map<Long, Long> result = new HashMap<>();
		for (Object[] row : rows) {
			result.put((Long) row[0], (Long) row[1]);
		}
		return result;
	}

	private static String name(String value, String label) {
		String trimmed = value == null ? "" : value.trim().replaceAll("\\s+", " ");
		if (trimmed.isEmpty()) {
			throw new IllegalArgumentException(label + " is required");
		}
		if (trimmed.length() > MAX_NAME_LENGTH) {
			throw new IllegalArgumentException(label + " can be at most " + MAX_NAME_LENGTH + " characters");
		}
		return trimmed;
	}
}
