package com.laundry.LaundryManagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.model.Expense;
import com.laundry.LaundryManagement.repository.ExpenseRepo;

/** Expense Track: an outlet's spending entries, totals and per-category subtotals. */
@Service
public class ExpenseService {

	public static final List<String> CATEGORIES = List.of("Supplies", "Utilities", "Rent", "Salary", "Maintenance",
			"Other");
	private static final int MAX_NAME_LENGTH = 100;
	private static final BigDecimal MAX_AMOUNT = new BigDecimal("9999999999.99");

	/** Dates are ISO strings (yyyy-MM-dd) so the JSON does not depend on Jackson's date settings. */
	public record ExpenseRow(Long id, String expenseDate, String expenseName, String category, BigDecimal amount,
			String createdBy) {
		static ExpenseRow of(Expense e) {
			return new ExpenseRow(e.getId(), e.getExpenseDate().toString(), e.getExpenseName(), e.getCategory(),
					e.getAmount(), e.getCreatedBy());
		}
	}

	public record ExpenseList(String from, String to, List<ExpenseRow> entries, BigDecimal total,
			Map<String, BigDecimal> byCategory) {
	}

	public record SaveExpenseRequest(Long id, String expenseDate, String expenseName, String category,
			BigDecimal amount) {
	}

	@Autowired
	private ExpenseRepo expenseRepo;

	/** Defaults to the current month when no dates are given. */
	public ExpenseList list(String from, String to, String category) {
		LocalDate start = isBlank(from) ? LocalDate.now().withDayOfMonth(1) : date(from, "From date");
		LocalDate end = isBlank(to) ? start.withDayOfMonth(start.lengthOfMonth()) : date(to, "To date");
		if (end.isBefore(start)) {
			throw new IllegalArgumentException("To date must be on or after From date");
		}
		String categoryFilter = isBlank(category) ? null : category(category);
		List<Expense> expenses = expenseRepo.findLive(start, end, categoryFilter);
		Map<String, BigDecimal> byCategory = new LinkedHashMap<>();
		BigDecimal total = BigDecimal.ZERO;
		for (String name : CATEGORIES) {
			BigDecimal sum = expenses.stream().filter(e -> e.getCategory().equals(name)).map(Expense::getAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			if (sum.signum() > 0) {
				byCategory.put(name, sum);
			}
			total = total.add(sum);
		}
		return new ExpenseList(start.toString(), end.toString(), expenses.stream().map(ExpenseRow::of).toList(),
				total, byCategory);
	}

	@Transactional
	public ExpenseRow save(SaveExpenseRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Expense details are required");
		}
		if (isBlank(request.expenseDate())) {
			throw new IllegalArgumentException("Date is required");
		}
		LocalDate date = date(request.expenseDate(), "Date");
		String name = request.expenseName() == null ? "" : request.expenseName().trim();
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Expense name is required");
		}
		if (name.length() > MAX_NAME_LENGTH) {
			throw new IllegalArgumentException("Expense name can be at most " + MAX_NAME_LENGTH + " characters");
		}
		if (isBlank(request.category())) {
			throw new IllegalArgumentException("Category is required");
		}
		String category = category(request.category());
		BigDecimal amount = request.amount();
		if (amount == null) {
			throw new IllegalArgumentException("Amount is required");
		}
		if (amount.signum() <= 0) {
			throw new IllegalArgumentException("Amount must be more than 0");
		}
		if (amount.stripTrailingZeros().scale() > 2) {
			throw new IllegalArgumentException("Amount can have at most 2 decimal places");
		}
		if (amount.compareTo(MAX_AMOUNT) > 0) {
			throw new IllegalArgumentException("Amount is too large");
		}

		String user = RequestScope.require().userName();
		Expense expense;
		if (request.id() == null) {
			expense = new Expense();
			expense.setCreatedBy(user);
			expense.setCreatedDate(new Date());
		} else {
			expense = live(request.id());
			expense.setUpdatedBy(user);
			expense.setUpdatedDate(new Date());
		}
		expense.setExpenseDate(date);
		expense.setExpenseName(name);
		expense.setCategory(category);
		expense.setAmount(amount.setScale(2));
		return ExpenseRow.of(expenseRepo.save(expense));
	}

	/** Removes the entry from the list and totals; the row is kept for the record. */
	@Transactional
	public void delete(long id) {
		Expense expense = live(id);
		expense.setActiveStatus(0);
		expense.setUpdatedBy(RequestScope.require().userName());
		expense.setUpdatedDate(new Date());
		expenseRepo.save(expense);
	}

	/** An existing, not deleted expense of the current outlet (other outlets' rows are filtered out). */
	private Expense live(long id) {
		Expense expense = expenseRepo.findById(id).orElse(null);
		if (expense == null || expense.getActiveStatus() != 1) {
			throw new IllegalArgumentException("Expense not found");
		}
		return expense;
	}

	private static String category(String value) {
		return CATEGORIES.stream().filter(c -> c.equalsIgnoreCase(value.trim())).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"Category must be one of " + String.join(", ", CATEGORIES)));
	}

	private static LocalDate date(String value, String label) {
		try {
			return LocalDate.parse(value.trim());
		} catch (DateTimeParseException ex) {
			throw new IllegalArgumentException(label + " must be a date in the form yyyy-MM-dd");
		}
	}

	private static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
