package com.laundry.LaundryManagement.excel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.auth.Permissions;
import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.auth.RolePermissions;
import com.laundry.LaundryManagement.excel.ExcelSheet.ExcelRow;
import com.laundry.LaundryManagement.excel.ExcelSheet.Parsed;
import com.laundry.LaundryManagement.excel.ImportValidationException.RowError;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Excel export and import of one Item Configuration screen, in the current outlet. Imports are
 * all-or-nothing: every row is validated first, and nothing is saved if any row has a problem.
 * Rows are matched to existing records by name (ignoring case); matches are updated, the rest added.
 */
public abstract class ExcelScreenHandler {

	public record ImportResult(int added, int updated, int unchanged) {
	}

	protected static final String STATUS = "Status";

	@PersistenceContext
	protected EntityManager em;

	@Autowired
	private RolePermissions rolePermissions;

	/** Key used in the URL, e.g. "category". */
	public abstract String key();

	/** Permission screen, e.g. "CATEGORY". */
	public abstract String permission();

	/** Sheet and file name, e.g. "Categories". */
	public abstract String label();

	public abstract List<String> headers();

	protected abstract List<List<Object>> exportRows();

	protected abstract ImportResult importRows(Parsed sheet);

	public byte[] export() {
		return ExcelSheet.write(label(), headers(), exportRows());
	}

	@Transactional
	public ImportResult importFile(InputStream in) {
		Parsed sheet = ExcelSheet.read(in);
		if (sheet.rows().isEmpty()) {
			throw new ImportValidationException("The file has no data rows below the headings");
		}
		return importRows(sheet);
	}

	// ---- Validation helpers ----

	/** Row problems collected before anything is saved. */
	protected static final class Errors {
		private final List<RowError> list = new ArrayList<>();

		public void add(int row, String message) {
			list.add(new RowError(row, message));
		}

		public void throwIfAny() {
			if (!list.isEmpty()) {
				throw new ImportValidationException(list);
			}
		}
	}

	protected static void requireColumns(Parsed sheet, String... columns) {
		Errors errors = new Errors();
		for (String column : columns) {
			if (!sheet.hasColumn(column)) {
				errors.add(1, "Column \"" + column + "\" is missing. Export the screen to get the expected columns.");
			}
		}
		errors.throwIfAny();
	}

	/** Required text; adds an error and returns null when blank. */
	protected static String required(ExcelRow row, String column, Errors errors) {
		String value = row.get(column);
		if (value.isEmpty()) {
			errors.add(row.number(), column + " is required");
			return null;
		}
		return value;
	}

	/** True for Active, false for Inactive, null when blank. */
	protected static Boolean status(ExcelRow row, Errors errors) {
		String value = row.get(STATUS).toLowerCase(Locale.ROOT);
		switch (value) {
		case "":
			return null;
		case "active":
			return true;
		case "inactive":
			return false;
		default:
			errors.add(row.number(), "Status must be Active or Inactive");
			return null;
		}
	}

	protected static boolean yesNo(ExcelRow row, String column, Errors errors) {
		String value = row.get(column).toLowerCase(Locale.ROOT);
		switch (value) {
		case "yes", "y", "true", "1":
			return true;
		case "", "no", "n", "false", "0":
			return false;
		default:
			errors.add(row.number(), column + " must be Yes or No");
			return false;
		}
	}

	/** A whole number of zero or more; null (with an error when required) if blank or invalid. */
	protected static Integer wholeNumber(ExcelRow row, String column, boolean isRequired, Errors errors) {
		String value = row.get(column);
		if (value.isEmpty()) {
			if (isRequired) {
				errors.add(row.number(), column + " is required");
			}
			return null;
		}
		try {
			double number = Double.parseDouble(value.replace(",", ""));
			if (number < 0 || number != Math.rint(number) || number > Integer.MAX_VALUE) {
				errors.add(row.number(), column + " must be a whole number of 0 or more");
				return null;
			}
			return (int) number;
		} catch (NumberFormatException ex) {
			errors.add(row.number(), column + " must be a number");
			return null;
		}
	}

	/** "percent" or "amount" for a Type cell holding Percentage/Percent or Amount. */
	protected static String chargeType(ExcelRow row, String column, Errors errors) {
		String value = row.get(column).toLowerCase(Locale.ROOT);
		if (value.isEmpty()) {
			errors.add(row.number(), column + " is required");
			return null;
		}
		if (value.equals("percentage") || value.equals("percent") || value.equals("%")) {
			return "percent";
		}
		if (value.equals("amount")) {
			return "amount";
		}
		errors.add(row.number(), column + " must be Percentage or Amount");
		return null;
	}

	/** What an import would do, for the permission check and the result shown to the user. */
	protected static final class Tally {
		private int added;
		private int edited;
		private int statusChanged;
		private int updated;
		private int unchanged;

		public void count(boolean isNew, boolean fieldsChanged, boolean statusChange) {
			if (isNew) {
				added++;
				return;
			}
			edited += fieldsChanged ? 1 : 0;
			statusChanged += statusChange ? 1 : 0;
			if (fieldsChanged || statusChange) {
				updated++;
			} else {
				unchanged++;
			}
		}

		public ImportResult result() {
			return new ImportResult(added, updated, unchanged);
		}
	}

	// ---- Permissions ----

	protected void checkPermissions(Tally tally, String noun) {
		checkPermissions(tally.added, tally.edited, tally.statusChanged, noun);
	}

	/** Refuses the whole import if the role lacks Create, Edit or Delete for what the file would change. */
	protected void checkPermissions(int added, int edited, int statusChanged, String noun) {
		List<String> missing = new ArrayList<>();
		if (added > 0 && !rolePermissions.has(permission(), Permissions.Action.CREATE)) {
			missing.add("add new " + noun + " (" + added + " in the file)");
		}
		if (edited > 0 && !rolePermissions.has(permission(), Permissions.Action.EDIT)) {
			missing.add("change existing " + noun + " (" + edited + " in the file)");
		}
		if (statusChanged > 0 && !rolePermissions.has(permission(), Permissions.Action.DELETE)) {
			missing.add("activate or deactivate " + noun + " (" + statusChanged + " in the file)");
		}
		if (!missing.isEmpty()) {
			throw new RolePermissions.PermissionDeniedException(
					"Your role cannot " + String.join(", ", missing) + ". Nothing was imported.");
		}
	}

	// ---- Formatting ----

	protected static String statusText(int activeStatus) {
		return activeStatus == 1 ? "Active" : "Inactive";
	}

	protected static String yesNoText(boolean value) {
		return value ? "Yes" : "No";
	}

	protected static String typeText(String type) {
		return "percent".equals(type) ? "Percentage" : "amount".equals(type) ? "Amount" : type;
	}

	protected static String key(String name) {
		return name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
	}

	protected static boolean same(Object a, Object b) {
		return Objects.equals(a == null || "".equals(a) ? null : a, b == null || "".equals(b) ? null : b);
	}

	protected static String userName() {
		return RequestScope.require().userName();
	}
}
