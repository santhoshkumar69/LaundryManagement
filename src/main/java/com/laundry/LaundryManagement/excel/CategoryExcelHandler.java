package com.laundry.LaundryManagement.excel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.laundry.LaundryManagement.excel.ExcelSheet.ExcelRow;
import com.laundry.LaundryManagement.excel.ExcelSheet.Parsed;
import com.laundry.LaundryManagement.model.ItemCategory;

@Component
public class CategoryExcelHandler extends ExcelScreenHandler {

	private static final String CODE = "Category Code";
	private static final String NAME = "Category Name";

	private record Plan(ItemCategory existing, String code, String name, Boolean active) {
	}

	@Override
	public String key() {
		return "category";
	}

	@Override
	public String permission() {
		return "CATEGORY";
	}

	@Override
	public String label() {
		return "Categories";
	}

	@Override
	public List<String> headers() {
		return List.of(CODE, NAME, STATUS);
	}

	@Override
	protected List<List<Object>> exportRows() {
		return categories().stream()
				.map(c -> List.<Object>of(nz(c.getCategoryCode()), c.getCategoryName(), statusText(c.getActiveStatus())))
				.toList();
	}

	@Override
	protected ImportResult importRows(Parsed sheet) {
		requireColumns(sheet, NAME);
		boolean hasCode = sheet.hasColumn(CODE);
		List<ItemCategory> all = categories();
		Map<String, ItemCategory> byName = new HashMap<>();
		Map<String, ItemCategory> byCode = new HashMap<>();
		for (ItemCategory c : all) {
			byName.put(key(c.getCategoryName()), c);
			if (c.getCategoryCode() != null && !c.getCategoryCode().isBlank()) {
				byCode.put(key(c.getCategoryCode()), c);
			}
		}

		Errors errors = new Errors();
		Set<String> namesInFile = new HashSet<>();
		Set<String> codesInFile = new HashSet<>();
		List<Plan> plans = new ArrayList<>();
		for (ExcelRow row : sheet.rows()) {
			String name = required(row, NAME, errors);
			Boolean active = status(row, errors);
			if (name == null) {
				continue;
			}
			if (!namesInFile.add(key(name))) {
				errors.add(row.number(), "Category " + name + " appears more than once in the file");
				continue;
			}
			ItemCategory existing = byName.get(key(name));
			String code = hasCode ? row.get(CODE) : existing == null ? "" : nz(existing.getCategoryCode());
			if (!code.isEmpty()) {
				ItemCategory owner = byCode.get(key(code));
				if (!codesInFile.add(key(code))) {
					errors.add(row.number(), "Category code " + code + " appears more than once in the file");
				} else if (owner != null && owner != existing) {
					errors.add(row.number(), "Category code " + code + " is already used by " + owner.getCategoryName());
				}
			}
			plans.add(new Plan(existing, code, name, active));
		}
		errors.throwIfAny();

		Tally tally = new Tally();
		for (Plan p : plans) {
			ItemCategory e = p.existing();
			tally.count(e == null,
					e != null && (!e.getCategoryName().equals(p.name()) || !same(e.getCategoryCode(), p.code())),
					e != null && p.active() != null && p.active() != (e.getActiveStatus() == 1));
		}
		checkPermissions(tally, "categories");

		for (Plan p : plans) {
			ItemCategory category = p.existing();
			if (category == null) {
				category = new ItemCategory();
				category.setCreatedBy(userName());
				category.setCreatedDate(new Date());
				category.setActiveStatus(1);
			} else {
				category.setUpdatedBy(userName());
				category.setUpdatedDate(new Date());
			}
			category.setCategoryName(p.name());
			category.setCategoryCode(p.code());
			if (p.active() != null) {
				category.setActiveStatus(p.active() ? 1 : 0);
			}
			if (category.getId() == null) {
				em.persist(category);
			}
		}
		return tally.result();
	}

	private List<ItemCategory> categories() {
		return em.createQuery("from ItemCategory c order by c.activeStatus desc, c.categoryName", ItemCategory.class)
				.getResultList();
	}

	private static String nz(String value) {
		return value == null ? "" : value;
	}
}
