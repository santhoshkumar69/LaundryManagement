package com.laundry.LaundryManagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.model.Expense;
import com.laundry.LaundryManagement.model.ExpenseCategory;
import com.laundry.LaundryManagement.model.ExpenseSubcategory;
import com.laundry.LaundryManagement.model.PaymentMode;
import com.laundry.LaundryManagement.repository.ExpenseRepo;
import com.laundry.LaundryManagement.repository.PaymentModeRepo;

/**
 * Expense Track: an outlet's spending entries, totals and per-category subtotals. Categories and subcategories are
 * chosen from the outlet's managed lists (Expense Categories).
 */
@Service
public class ExpenseService {

	private static final int MAX_NAME_LENGTH = 100;
	private static final int MAX_RECEIPT_LENGTH = 50;
	private static final BigDecimal MAX_AMOUNT = new BigDecimal("9999999999.99");
	/** Subtotal key for entries saved before subcategories existed. */
	public static final String NO_SUBCATEGORY = "Not set";

	/** Dates are ISO strings (yyyy-MM-dd) so the JSON does not depend on Jackson's date settings. */
	public record ExpenseRow(Long id, String expenseDate, String expenseName, Long categoryId, String category,
			Long subcategoryId, String subcategory, BigDecimal amount, String receiptNo, Long paymentModeId,
			String paymentMode, String createdBy, String createdByLogin) {
		static ExpenseRow of(Expense e) {
			return new ExpenseRow(e.getId(), e.getExpenseDate().toString(), e.getExpenseName(), e.getCategoryId(),
					e.getCategory(), e.getSubcategoryId(), e.getSubcategory(), e.getAmount(), e.getReceiptNo(),
					e.getPaymentModeId(), e.getPaymentMode(), e.getCreatedBy(), e.getCreatedByLogin());
		}
	}

	public record ExpenseList(String from, String to, List<ExpenseRow> entries, BigDecimal total,
			Map<String, BigDecimal> byCategory, Map<String, Map<String, BigDecimal>> bySubcategory) {
	}

	/** createdBy is not accepted here: it always comes from the signed-in user. */
	public record SaveExpenseRequest(Long id, String expenseDate, String expenseName, Long categoryId,
			Long subcategoryId, BigDecimal amount, String receiptNo, Long paymentModeId, String paymentMode) {
	}

	public record Option(Long id, String name) {
	}

	public record CategoryOption(Long id, String name, List<Option> subcategories) {
	}

	public record PaymentModeOption(Long id, String name, boolean isDefault) {
	}

	/** Choices for the expense form and the category filter: active categories and payment modes only. */
	public record ExpenseOptions(List<CategoryOption> categories, List<PaymentModeOption> paymentModes,
			String defaultPaymentMode) {
	}

	@Autowired
	private ExpenseRepo expenseRepo;

	@Autowired
	private PaymentModeRepo paymentModeRepo;

	@Autowired
	private ExpenseCategoryService categoryService;

	/** Defaults to the current month when no dates are given. */
	public ExpenseList list(String from, String to, Long categoryId, String category) {
		LocalDate start = isBlank(from) ? LocalDate.now().withDayOfMonth(1) : date(from, "From date");
		LocalDate end = isBlank(to) ? start.withDayOfMonth(start.lengthOfMonth()) : date(to, "To date");
		if (end.isBefore(start)) {
			throw new IllegalArgumentException("To date must be on or after From date");
		}
		List<Expense> expenses = expenseRepo.findLive(start, end, categoryId,
				isBlank(category) ? null : category.trim());
		Map<String, BigDecimal> byCategory = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		Map<String, Map<String, BigDecimal>> bySubcategory = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		BigDecimal total = BigDecimal.ZERO;
		for (Expense e : expenses) {
			byCategory.merge(e.getCategory(), e.getAmount(), BigDecimal::add);
			String sub = isBlank(e.getSubcategory()) ? NO_SUBCATEGORY : e.getSubcategory();
			bySubcategory.computeIfAbsent(e.getCategory(), k -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER))
					.merge(sub, e.getAmount(), BigDecimal::add);
			total = total.add(e.getAmount());
		}
		return new ExpenseList(start.toString(), end.toString(), expenses.stream().map(ExpenseRow::of).toList(),
				total, new LinkedHashMap<>(byCategory), new LinkedHashMap<>(bySubcategory));
	}

	public ExpenseOptions options() {
		List<CategoryOption> categories = categoryService.list(false).stream()
				.map(c -> new CategoryOption(c.id(), c.name(),
						c.subcategories().stream().map(s -> new Option(s.id(), s.name())).toList()))
				.toList();
		List<PaymentModeOption> modes = paymentModeRepo.findActivePaymentMode().stream()
				.map(m -> new PaymentModeOption(m.getId(), m.getPaymentMode(), m.isDefaultMode())).toList();
		String defaultMode = modes.stream().filter(PaymentModeOption::isDefault).map(PaymentModeOption::name)
				.findFirst().orElse(null);
		return new ExpenseOptions(categories, modes, defaultMode);
	}

	public List<String> categoryNames() {
		return categoryService.list(false).stream().map(ExpenseCategoryService.CategoryView::name).toList();
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
		String name = text(request.expenseName(), "Expense name", MAX_NAME_LENGTH, true);
		if (request.categoryId() == null) {
			throw new IllegalArgumentException("Category is required");
		}
		if (request.subcategoryId() == null) {
			throw new IllegalArgumentException("Subcategory is required");
		}
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
		String receiptNo = text(request.receiptNo(), "Receipt number", MAX_RECEIPT_LENGTH, false);

		RequestScope.Scope user = RequestScope.require();
		Expense expense;
		if (request.id() == null) {
			expense = new Expense();
			expense.setCreatedBy(user.userName());
			expense.setCreatedByLogin(user.loginId());
			expense.setCreatedDate(new Date());
		} else {
			expense = live(request.id());
			expense.setUpdatedBy(user.userName());
			expense.setUpdatedDate(new Date());
		}
		applyCategory(expense, request.categoryId(), request.subcategoryId());
		applyPaymentMode(expense, request.paymentModeId(), request.paymentMode());
		expense.setExpenseDate(date);
		expense.setExpenseName(name);
		expense.setAmount(amount.setScale(2));
		expense.setReceiptNo(receiptNo);
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

	/**
	 * Category and subcategory must be the outlet's own and active, and the subcategory must sit under the category.
	 * An entry may keep a category or subcategory that was deleted after it was recorded.
	 */
	private void applyCategory(Expense expense, Long categoryId, Long subcategoryId) {
		ExpenseCategory category = categoryService.category(categoryId);
		if (category.getActiveStatus() != 1 && !Objects.equals(categoryId, expense.getCategoryId())) {
			throw new IllegalArgumentException("The category \"" + category.getName() + "\" has been deleted");
		}
		ExpenseSubcategory subcategory = categoryService.subcategory(subcategoryId);
		if (!Objects.equals(subcategory.getCategoryId(), category.getId())) {
			throw new IllegalArgumentException("The subcategory does not belong to \"" + category.getName() + "\"");
		}
		if (subcategory.getActiveStatus() != 1 && !Objects.equals(subcategoryId, expense.getSubcategoryId())) {
			throw new IllegalArgumentException("The subcategory \"" + subcategory.getName() + "\" has been deleted");
		}
		expense.setCategoryId(category.getId());
		expense.setCategory(category.getName());
		expense.setSubcategoryId(subcategory.getId());
		expense.setSubcategory(subcategory.getName());
	}

	/**
	 * Payment type is optional; when given it must be one of the outlet's active payment modes. An entry keeps the
	 * mode it already has even if that mode was renamed or removed since.
	 */
	private void applyPaymentMode(Expense expense, Long id, String rawName) {
		String name = rawName == null ? "" : rawName.trim();
		if (id == null && name.isEmpty()) {
			expense.setPaymentModeId(null);
			expense.setPaymentMode(null);
			return;
		}
		PaymentMode match = paymentModeRepo.findActivePaymentMode().stream()
				.filter(m -> id != null ? id.equals(m.getId()) : name.equalsIgnoreCase(m.getPaymentMode()))
				.findFirst().orElse(null);
		if (match != null) {
			expense.setPaymentModeId(match.getId());
			expense.setPaymentMode(match.getPaymentMode());
			return;
		}
		boolean unchanged = expense.getId() != null
				&& (id != null ? id.equals(expense.getPaymentModeId()) : name.equalsIgnoreCase(expense.getPaymentMode()));
		if (!unchanged) {
			throw new IllegalArgumentException("Payment type must be one of this outlet's payment modes");
		}
	}

	/** An existing, not deleted expense of the current outlet (other outlets' rows are filtered out). */
	private Expense live(long id) {
		Expense expense = expenseRepo.findById(id).orElse(null);
		if (expense == null || expense.getActiveStatus() != 1) {
			throw new IllegalArgumentException("Expense not found");
		}
		return expense;
	}

	private static String text(String value, String label, int maxLength, boolean required) {
		String trimmed = value == null ? "" : value.trim().replaceAll("\\s+", " ");
		if (trimmed.isEmpty()) {
			if (required) {
				throw new IllegalArgumentException(label + " is required");
			}
			return null;
		}
		if (trimmed.length() > maxLength) {
			throw new IllegalArgumentException(label + " can be at most " + maxLength + " characters");
		}
		return trimmed;
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
