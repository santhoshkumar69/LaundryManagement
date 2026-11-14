package com.laundry.LaundryManagement.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.laundry.LaundryManagement.excel.ExcelSheet.ExcelRow;
import com.laundry.LaundryManagement.excel.ExcelSheet.Parsed;

/** Extra charges and discounts: a unique name, Percentage/Amount type, whole-number value and flags. */
abstract class AdjustmentExcelHandler<T> extends ExcelScreenHandler {

	protected static final String TYPE = "Type";
	protected static final String VALUE = "Value";
	protected static final String OPEN = "Open";
	protected static final String REMARK_REQUIRED = "Remark Required";

	protected record Values(String name, String type, int value, boolean open, boolean remarkRequired) {
	}

	private record Plan<T>(T existing, Values values, Boolean active) {
	}

	protected abstract String nameColumn();

	protected abstract String noun();

	/** Discounts can require a remark; extra charges cannot. */
	protected abstract boolean hasRemarkRequired();

	protected abstract List<T> all();

	protected abstract Values values(T record);

	protected abstract int activeStatus(T record);

	protected abstract T create();

	protected abstract void apply(T record, Values values, Integer activeStatus, boolean isNew);

	@Override
	public List<String> headers() {
		List<String> headers = new ArrayList<>(List.of(nameColumn(), TYPE, VALUE, OPEN));
		if (hasRemarkRequired()) {
			headers.add(REMARK_REQUIRED);
		}
		headers.add(STATUS);
		return headers;
	}

	@Override
	protected List<List<Object>> exportRows() {
		List<List<Object>> rows = new ArrayList<>();
		for (T record : all()) {
			Values v = values(record);
			List<Object> row = new ArrayList<>(List.of(v.name(), typeText(v.type()), v.value(), yesNoText(v.open())));
			if (hasRemarkRequired()) {
				row.add(yesNoText(v.remarkRequired()));
			}
			row.add(statusText(activeStatus(record)));
			rows.add(row);
		}
		return rows;
	}

	@Override
	protected ImportResult importRows(Parsed sheet) {
		requireColumns(sheet, nameColumn(), TYPE, VALUE);
		Map<String, T> byName = new HashMap<>();
		all().forEach(r -> byName.put(key(values(r).name()), r));

		Errors errors = new Errors();
		Set<String> namesInFile = new HashSet<>();
		List<Plan<T>> plans = new ArrayList<>();
		for (ExcelRow row : sheet.rows()) {
			String name = required(row, nameColumn(), errors);
			String type = chargeType(row, TYPE, errors);
			Integer value = wholeNumber(row, VALUE, true, errors);
			boolean open = yesNo(row, OPEN, errors);
			boolean remark = hasRemarkRequired() && yesNo(row, REMARK_REQUIRED, errors);
			Boolean active = status(row, errors);
			if (name == null || type == null || value == null) {
				continue;
			}
			if (!namesInFile.add(key(name))) {
				errors.add(row.number(), nameColumn() + " " + name + " appears more than once in the file");
				continue;
			}
			T existing = byName.get(key(name));
			if (existing != null && !sheet.hasColumn(OPEN)) {
				open = values(existing).open();
			}
			if (existing != null && hasRemarkRequired() && !sheet.hasColumn(REMARK_REQUIRED)) {
				remark = values(existing).remarkRequired();
			}
			plans.add(new Plan<>(existing, new Values(name, type, value, open, remark), active));
		}
		errors.throwIfAny();

		Tally tally = new Tally();
		for (Plan<T> p : plans) {
			T e = p.existing();
			tally.count(e == null, e != null && !values(e).equals(p.values()),
					e != null && p.active() != null && p.active() != (activeStatus(e) == 1));
		}
		checkPermissions(tally, noun());

		for (Plan<T> p : plans) {
			Integer status = p.active() == null ? null : p.active() ? 1 : 0;
			if (p.existing() == null) {
				T record = create();
				apply(record, p.values(), status == null ? 1 : status, true);
				em.persist(record);
			} else {
				apply(p.existing(), p.values(), status, false);
			}
		}
		return tally.result();
	}
}
