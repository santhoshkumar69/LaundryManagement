package com.laundry.LaundryManagement.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.laundry.LaundryManagement.excel.ExcelSheet.ExcelRow;
import com.laundry.LaundryManagement.excel.ExcelSheet.Parsed;

/** Screens whose records are just a unique name and an active status (units, laundry types). */
abstract class NamedExcelHandler<T> extends ExcelScreenHandler {

	private record Plan<T>(T existing, String name, Boolean active) {
	}

	/** Column holding the name, e.g. "Unit". */
	protected abstract String nameColumn();

	/** Plural noun for messages, e.g. "units". */
	protected abstract String noun();

	protected abstract List<T> all();

	protected abstract String name(T record);

	protected abstract int activeStatus(T record);

	/** A new record, not yet saved. */
	protected abstract T create(String name);

	/** Applies the name and status to a saved or new record and stamps who changed it. */
	protected abstract void apply(T record, String name, Integer activeStatus, boolean isNew);

	@Override
	public List<String> headers() {
		return List.of(nameColumn(), STATUS);
	}

	@Override
	protected List<List<Object>> exportRows() {
		return all().stream().map(r -> List.<Object>of(name(r), statusText(activeStatus(r)))).toList();
	}

	@Override
	protected ImportResult importRows(Parsed sheet) {
		requireColumns(sheet, nameColumn());
		Map<String, T> byName = new HashMap<>();
		all().forEach(r -> byName.put(key(name(r)), r));

		Errors errors = new Errors();
		Set<String> namesInFile = new HashSet<>();
		List<Plan<T>> plans = new ArrayList<>();
		for (ExcelRow row : sheet.rows()) {
			String name = required(row, nameColumn(), errors);
			Boolean active = status(row, errors);
			if (name == null) {
				continue;
			}
			if (!namesInFile.add(key(name))) {
				errors.add(row.number(), nameColumn() + " " + name + " appears more than once in the file");
				continue;
			}
			plans.add(new Plan<>(byName.get(key(name)), name, active));
		}
		errors.throwIfAny();

		Tally tally = new Tally();
		for (Plan<T> p : plans) {
			T e = p.existing();
			tally.count(e == null, e != null && !name(e).equals(p.name()),
					e != null && p.active() != null && p.active() != (activeStatus(e) == 1));
		}
		checkPermissions(tally, noun());

		for (Plan<T> p : plans) {
			Integer status = p.active() == null ? null : p.active() ? 1 : 0;
			if (p.existing() == null) {
				T record = create(p.name());
				apply(record, p.name(), status == null ? 1 : status, true);
				em.persist(record);
			} else {
				apply(p.existing(), p.name(), status, false);
			}
		}
		return tally.result();
	}
}
