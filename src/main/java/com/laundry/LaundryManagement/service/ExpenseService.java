package com.laundry.LaundryManagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.model.Expense;
import com.laundry.LaundryManagement.model.PaymentMode;
import com.laundry.LaundryManagement.repository.ExpenseRepo;
import com.laundry.LaundryManagement.repository.PaymentModeRepo;

/**
 * Expense Track: an outlet's spending entries, totals and per-category subtotals. Categories and subcategories are
 * whatever the outlet has used so far; a new one is created by simply using it.
 */
@Service
public class ExpenseService {

	private static final int MAX_NAME_LENGTH = 100;
	private static final int MAX_CATEGORY_LENGTH = 100;
	private static final int MAX_RECEIPT_LENGTH = 50;
	private static final BigDecimal MAX_AMOUNT = new BigDecimal("9999999999.99");
	/** Subtotal key for entries saved before subcategories existed. */
	public static final String NO_SUBCATEGORY = "Not set";

	/** Dates are ISO strings (yyyy-MM-dd) so the JSON does not depend on Jackson's date settings. */
	public record ExpenseRow(Long id, String expenseDate, String expenseName, String category, String subcategory,
			BigDecimal amount, String receiptNo, Long paymentModeId, String paymentMode, String createdBy,
			String createdByLogin) {
		static ExpenseRow of(Expense e) {
			return new ExpenseRow(e.getId(), e.getExpenseDate().toString(), e.getExpenseName(), e.getCategory(),
					e.getSubcategory(), e.getAmount(), e.getReceiptNo(), e.getPaymentModeId(), e.getPaymentMode(),
					e.getCreatedBy(), e.getCreatedByLogin());
		}
	}

	public record ExpenseList(String from, String to, List<ExpenseRow> entries, BigDecimal total,
			Map<String, BigDecimal> byCategory, Map<String, Map<String, BigDecimal>> bySubcategory) {
	}

	/** createdBy is not accepted here: it always comes from the signed-in user. */
	public record SaveExpenseRequest(Long id, String expenseDate, String expenseName, String category,
			String subcategory, BigDecimal amount, String receiptNo, Long paymentModeId, String paymentMode) {
	}

	public record CategoryOption(String name, List<String> subcategories) {
	}

	public record PaymentModeOption(Long id, String name, boolean isDefault) {
	}

	/** Suggestions for the expense form and the category filter. */
	public record ExpenseOptions(List<CategoryOption> categories, List<PaymentModeOption> paymentModes,
			String defaultPaymentMode) {
	}

	@Autowired
	private ExpenseRepo expenseRepo;

	@Autowired
	private PaymentModeRepo paymentModeRepo;

	/** Defaults to the current month when no dates are given. */
	public ExpenseList list(String from, String to, String category) {
		LocalDate start = isBlank(from) ? LocalDate.now().withDayOfMonth(1) : date(from, "From date");
		LocalDate end = isBlank(to) ? start.withDayOfMonth(start.lengthOfMonth()) : date(to, "To date");
		if (end.isBefore(start)) {
			throw new IllegalArgumentException("To date must be on or after From date");
		}
		List<Expense> expenses = expenseRepo.findLive(start, end, isBlank(category) ? null : category.trim());
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

	/** Categories (each with its subcategories) used in this outlet, plus its active payment modes. */
	public ExpenseOptions options() {
		List<CategoryOption> categories = knownCategories(null).values().stream()
				.map(c -> new CategoryOption(c.name, new ArrayList<>(c.subcategories.values()))).toList();
		List<PaymentModeOption> modes = paymentModeRepo.findActivePaymentMode().stream()
				.map(m -> new PaymentModeOption(m.getId(), m.getPaymentMode(), m.isDefaultMode())).toList();
		String defaultMode = modes.stream().filter(PaymentModeOption::isDefault).map(PaymentModeOption::name)
				.findFirst().orElse(null);
		return new ExpenseOptions(categories, modes, defaultMode);
	}

	public List<String> categoryNames() {
		return knownCategories(null).values().stream().map(c -> c.name).toList();
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
		String category = text(request.category(), "Category", MAX_CATEGORY_LENGTH, true);
		String subcategory = text(request.subcategory(), "Subcategory", MAX_CATEGORY_LENGTH, true);
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
		applyPaymentMode(expense, request.paymentModeId(), request.paymentMode());

		// Reuse the casing the outlet already uses, so "rent" and "Rent" stay one category.
		KnownCategory known = knownCategories(expense.getId()).get(key(category));
		if (known != null) {
			category = known.name;
			subcategory = known.subcategories.getOrDefault(key(subcategory), subcategory);
		}
		expense.setExpenseDate(date);
		expense.setExpenseName(name);
		expense.setCategory(category);
		expense.setSubcategory(subcategory);
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

	private static final class KnownCategory {
		final String name;
		final Map<String, String> subcategories = new LinkedHashMap<>();

		KnownCategory(String name) {
			this.name = name;
		}
	}

	/** Categories by lower-case key, keeping the first-used casing; skips the entry being edited. */
	private Map<String, KnownCategory> knownCategories(Long excludeId) {
		Map<String, KnownCategory> known = new LinkedHashMap<>();
		for (Object[] row : expenseRepo.findCategoryPairs()) {
			if (excludeId != null && Objects.equals(excludeId, row[0])) {
				continue;
			}
			String category = (String) row[1];
			String sub = (String) row[2];
			if (isBlank(category)) {
				continue;
			}
			KnownCategory entry = known.computeIfAbsent(key(category), k -> new KnownCategory(category.trim()));
			if (!isBlank(sub)) {
				entry.subcategories.putIfAbsent(key(sub), sub.trim());
			}
		}
		List<Map.Entry<String, KnownCategory>> sorted = new ArrayList<>(known.entrySet());
		sorted.sort(Map.Entry.comparingByKey());
		Map<String, KnownCategory> result = new LinkedHashMap<>();
		for (Map.Entry<String, KnownCategory> e : sorted) {
			List<Map.Entry<String, String>> subs = new ArrayList<>(e.getValue().subcategories.entrySet());
			subs.sort(Map.Entry.comparingByKey());
			e.getValue().subcategories.clear();
			subs.forEach(s -> e.getValue().subcategories.put(s.getKey(), s.getValue()));
			result.put(e.getKey(), e.getValue());
		}
		return result;
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

	private static String key(String value) {
		return value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
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
