package com.laundry.LaundryManagement.excel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.laundry.LaundryManagement.excel.ExcelSheet.ExcelRow;
import com.laundry.LaundryManagement.excel.ExcelSheet.Parsed;
import com.laundry.LaundryManagement.model.ItemCategory;
import com.laundry.LaundryManagement.model.ItemMaster;
import com.laundry.LaundryManagement.model.ItemMasterPriceDetails;
import com.laundry.LaundryManagement.model.Unit;
import com.laundry.LaundryManagement.model.WashingType;

/**
 * One row per price line; the item's details repeat on each of its rows. An imported item's price
 * lines replace the ones it had.
 */
@Component
public class ItemsExcelHandler extends ExcelScreenHandler {

	private static final String CODE = "Item Code";
	private static final String NAME = "Item Name";
	private static final String ALT_NAME = "Alt Name";
	private static final String CATEGORY = "Category";
	private static final String FREQUENT = "Frequently Used";
	private static final String LAUNDRY_TYPE = "Laundry Type";
	private static final String UNIT = "Unit";
	private static final String PRICE = "Price";
	private static final String URGENT_PRICE = "Urgent Price";

	private record Price(int washingTypeId, int unitId, int price, int urgentPrice) {
	}

	private record ItemValues(String code, String name, String altName, int categoryId, boolean frequent) {
	}

	private record Plan(ItemMaster existing, ItemValues values, Boolean active, Set<Price> prices) {
	}

	@Override
	public String key() {
		return "items";
	}

	@Override
	public String permission() {
		return "ITEMS";
	}

	@Override
	public String label() {
		return "Items";
	}

	@Override
	public List<String> headers() {
		return List.of(CODE, NAME, ALT_NAME, CATEGORY, FREQUENT, LAUNDRY_TYPE, UNIT, PRICE, URGENT_PRICE, STATUS);
	}

	@Override
	protected List<List<Object>> exportRows() {
		Map<Integer, String> categories = categories().stream()
				.collect(Collectors.toMap(ItemCategory::getId, ItemCategory::getCategoryName));
		Map<Integer, String> types = washingTypes().stream()
				.collect(Collectors.toMap(WashingType::getProcessTypeId, WashingType::getProcessTypeName));
		Map<Integer, String> units = units().stream().collect(Collectors.toMap(Unit::getId, Unit::getUnit));
		Map<Integer, List<ItemMasterPriceDetails>> pricesByItem = prices().stream()
				.collect(Collectors.groupingBy(ItemMasterPriceDetails::getItemMasterId));

		List<List<Object>> rows = new ArrayList<>();
		for (ItemMaster item : items()) {
			List<Object> details = List.of(nz(item.getItemCode()), item.getItemName(), nz(item.getArabicName()),
					categories.getOrDefault(item.getItemCategoryid(), ""), yesNoText(item.isFrequentlyUsed()));
			List<ItemMasterPriceDetails> lines = pricesByItem.getOrDefault(item.getId(), List.of());
			if (lines.isEmpty()) {
				List<Object> row = new ArrayList<>(details);
				row.addAll(List.of("", "", "", "", statusText(item.getActiveStatus())));
				rows.add(row);
			}
			for (ItemMasterPriceDetails line : lines) {
				List<Object> row = new ArrayList<>(details);
				row.add(types.getOrDefault(line.getWashingTypeId(), ""));
				row.add(units.getOrDefault(line.getUnitId(), ""));
				row.add(line.getOrdinaryPrice());
				row.add(line.getUrgentPrice());
				row.add(statusText(item.getActiveStatus()));
				rows.add(row);
			}
		}
		return rows;
	}

	@Override
	protected ImportResult importRows(Parsed sheet) {
		requireColumns(sheet, NAME, CATEGORY, LAUNDRY_TYPE, UNIT, PRICE);
		List<ItemMaster> items = items();
		Map<String, ItemMaster> itemsByName = new HashMap<>();
		Map<String, ItemMaster> itemsByCode = new HashMap<>();
		for (ItemMaster item : items) {
			itemsByName.put(key(item.getItemName()), item);
			if (item.getItemCode() != null && !item.getItemCode().isBlank()) {
				itemsByCode.put(key(item.getItemCode()), item);
			}
		}
		Map<String, ItemCategory> categories = new HashMap<>();
		categories().forEach(c -> categories.put(key(c.getCategoryName()), c));
		Map<String, WashingType> types = new HashMap<>();
		washingTypes().forEach(t -> types.put(key(t.getProcessTypeName()), t));
		Map<String, Unit> units = new HashMap<>();
		units().forEach(u -> units.put(key(u.getUnit()), u));
		Map<Integer, Set<Price>> currentPrices = prices().stream().collect(Collectors.groupingBy(
				ItemMasterPriceDetails::getItemMasterId, Collectors.mapping(ItemsExcelHandler::price, Collectors.toSet())));

		Errors errors = new Errors();
		Map<String, List<ExcelRow>> rowsByItem = new LinkedHashMap<>();
		for (ExcelRow row : sheet.rows()) {
			String name = required(row, NAME, errors);
			if (name != null) {
				rowsByItem.computeIfAbsent(key(name), k -> new ArrayList<>()).add(row);
			}
		}

		Set<String> codesInFile = new HashSet<>();
		List<Plan> plans = new ArrayList<>();
		for (List<ExcelRow> itemRows : rowsByItem.values()) {
			ExcelRow first = itemRows.get(0);
			String name = first.get(NAME);
			ItemMaster existing = itemsByName.get(key(name));
			for (String column : List.of(CODE, ALT_NAME, CATEGORY, FREQUENT, STATUS)) {
				for (ExcelRow other : itemRows.subList(1, itemRows.size())) {
					if (!other.get(column).isEmpty() && !other.get(column).equalsIgnoreCase(first.get(column))) {
						errors.add(other.number(), "Item " + name + " has a different " + column + " than on row "
								+ first.number() + "; an item's details must be the same on all its rows");
					}
				}
			}

			String code = sheet.hasColumn(CODE) ? first.get(CODE) : existing == null ? "" : nz(existing.getItemCode());
			if (!code.isEmpty()) {
				ItemMaster owner = itemsByCode.get(key(code));
				if (!codesInFile.add(key(code))) {
					errors.add(first.number(), "Item code " + code + " appears on more than one item in the file");
				} else if (owner != null && owner != existing) {
					errors.add(first.number(), "Item code " + code + " is already used by " + owner.getItemName());
				}
			}
			String categoryName = required(first, CATEGORY, errors);
			ItemCategory category = categoryName == null ? null : categories.get(key(categoryName));
			if (categoryName != null && category == null) {
				errors.add(first.number(), "Category " + categoryName + " does not exist; add it under Category first");
			} else if (category != null && category.getActiveStatus() != 1) {
				errors.add(first.number(), "Category " + categoryName + " is inactive");
			}
			boolean frequent = sheet.hasColumn(FREQUENT) ? yesNo(first, FREQUENT, errors)
					: existing != null && existing.isFrequentlyUsed();
			String altName = sheet.hasColumn(ALT_NAME) ? first.get(ALT_NAME) : existing == null ? "" : nz(existing.getArabicName());
			Boolean active = status(first, errors);

			Set<Price> prices = new HashSet<>();
			Set<String> lineKeys = new HashSet<>();
			for (ExcelRow row : itemRows) {
				String typeName = required(row, LAUNDRY_TYPE, errors);
				String unitName = required(row, UNIT, errors);
				Integer price = wholeNumber(row, PRICE, true, errors);
				Integer urgent = wholeNumber(row, URGENT_PRICE, false, errors);
				WashingType type = typeName == null ? null : types.get(key(typeName));
				Unit unit = unitName == null ? null : units.get(key(unitName));
				if (typeName != null && (type == null || type.getActiveStatus() != 1)) {
					errors.add(row.number(), "Laundry type " + typeName + (type == null ? " does not exist" : " is inactive"));
				}
				if (unitName != null && (unit == null || unit.getActiveStatus() != 1)) {
					errors.add(row.number(), "Unit " + unitName + (unit == null ? " does not exist" : " is inactive"));
				}
				if (type == null || unit == null || price == null) {
					continue;
				}
				if (!lineKeys.add(type.getProcessTypeId() + "|" + unit.getId())) {
					errors.add(row.number(), "Item " + name + " has " + typeName + " / " + unitName + " more than once");
					continue;
				}
				prices.add(new Price(type.getProcessTypeId(), unit.getId(), price, urgent == null ? 0 : urgent));
			}
			if (category != null) {
				plans.add(new Plan(existing, new ItemValues(code, name, altName, category.getId(), frequent), active, prices));
			}
		}
		errors.throwIfAny();

		Tally tally = new Tally();
		for (Plan p : plans) {
			ItemMaster e = p.existing();
			boolean changed = e != null && (!values(e).equals(p.values())
					|| !currentPrices.getOrDefault(e.getId(), Set.of()).equals(p.prices()));
			tally.count(e == null, changed, e != null && p.active() != null && p.active() != (e.getActiveStatus() == 1));
		}
		checkPermissions(tally, "items");

		for (Plan p : plans) {
			ItemMaster item = p.existing();
			boolean isNew = item == null;
			if (isNew) {
				item = new ItemMaster();
				item.setActiveStatus(1);
				item.setUserID(userName());
				item.setCreatedBy(userName());
				item.setCreatedDate(new Date());
			} else {
				item.setUpdatedBy(userName());
				item.setUpdatedDate(new Date());
			}
			item.setItemCode(p.values().code());
			item.setItemName(p.values().name());
			item.setArabicName(p.values().altName());
			item.setItemCategoryid(p.values().categoryId());
			item.setFrequentlyUsed(p.values().frequent());
			if (p.active() != null) {
				item.setActiveStatus(p.active() ? 1 : 0);
			}
			if (isNew) {
				em.persist(item);
				em.flush();
			} else if (!currentPrices.getOrDefault(item.getId(), Set.of()).equals(p.prices())) {
				em.createQuery("delete from ItemMasterPriceDetails d where d.itemMasterId = :itemId")
						.setParameter("itemId", item.getId()).executeUpdate();
			} else {
				continue;
			}
			for (Price price : p.prices()) {
				ItemMasterPriceDetails line = new ItemMasterPriceDetails();
				line.setItemMasterId(item.getId());
				line.setWashingTypeId(price.washingTypeId());
				line.setUnitId(price.unitId());
				line.setOrdinaryPrice(price.price());
				line.setUrgentPrice(price.urgentPrice());
				em.persist(line);
			}
		}
		return tally.result();
	}

	private static ItemValues values(ItemMaster item) {
		return new ItemValues(nz(item.getItemCode()), item.getItemName(), nz(item.getArabicName()),
				item.getItemCategoryid(), item.isFrequentlyUsed());
	}

	private static Price price(ItemMasterPriceDetails line) {
		return new Price(line.getWashingTypeId(), line.getUnitId(), line.getOrdinaryPrice(), line.getUrgentPrice());
	}

	private List<ItemMaster> items() {
		return em.createQuery("from ItemMaster i order by i.activeStatus desc, i.itemName", ItemMaster.class)
				.getResultList();
	}

	private List<ItemCategory> categories() {
		return em.createQuery("from ItemCategory c", ItemCategory.class).getResultList();
	}

	private List<WashingType> washingTypes() {
		return em.createQuery("from WashingType w", WashingType.class).getResultList();
	}

	private List<Unit> units() {
		return em.createQuery("from Unit u", Unit.class).getResultList();
	}

	private List<ItemMasterPriceDetails> prices() {
		return em.createQuery("from ItemMasterPriceDetails p order by p.ID", ItemMasterPriceDetails.class).getResultList();
	}

	private static String nz(String value) {
		return value == null ? "" : value;
	}
}
