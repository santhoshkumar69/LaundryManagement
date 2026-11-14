package com.laundry.LaundryManagement.auth;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Screens a custom role can be granted, and the actions each one supports. A permission code is
 * {@code SCREEN.ACTION}, e.g. {@code CATEGORY.CREATE}. Admins always have every permission.
 */
public final class Permissions {

	public enum Action {
		VIEW, CREATE, EDIT, DELETE
	}

	public record Screen(String key, String group, String label, List<Action> actions) {
	}

	private static final List<Action> ALL_ACTIONS = List.of(Action.values());

	public static final List<Screen> SCREENS = List.of(
			new Screen("CATEGORY", "Item Configuration", "Category", ALL_ACTIONS),
			new Screen("UNIT", "Item Configuration", "Unit", ALL_ACTIONS),
			new Screen("LAUNDRY_TYPE", "Item Configuration", "Laundry Type", ALL_ACTIONS),
			new Screen("ITEMS", "Item Configuration", "Manage Items", ALL_ACTIONS),
			new Screen("EXTRA_CHARGES", "Item Configuration", "Manage Extra Charges", ALL_ACTIONS),
			new Screen("DISCOUNTS", "Item Configuration", "Manage Discounts", ALL_ACTIONS),
			new Screen("RECEIVING", "Bill", "Receiving", List.of(Action.VIEW, Action.CREATE)),
			new Screen("ORDER_STATUS", "Bill", "Order Status", List.of(Action.VIEW, Action.EDIT, Action.DELETE)),
			new Screen("DELIVERY", "Bill", "Delivery", List.of(Action.VIEW, Action.EDIT, Action.DELETE)),
			new Screen("CUSTOMERS", "Customer", "Manage Customer", List.of(Action.VIEW, Action.CREATE, Action.DELETE)),
			new Screen("INVOICE_TEMPLATE", "Settings", "Invoice Template", ALL_ACTIONS));

	private static final Set<String> ALL_CODES = SCREENS.stream()
			.flatMap(screen -> screen.actions().stream().map(action -> code(screen.key(), action)))
			.collect(Collectors.toCollection(LinkedHashSet::new));

	private Permissions() {
	}

	public static String code(String screen, Action action) {
		return screen + "." + action;
	}

	public static Set<String> all() {
		return ALL_CODES;
	}

	/** Known codes only, with View added for any screen that has another action. */
	public static Set<String> normalize(Collection<String> codes) {
		Set<String> result = new TreeSet<>();
		if (codes == null) {
			return result;
		}
		for (String raw : codes) {
			String code = raw == null ? "" : raw.trim().toUpperCase();
			if (ALL_CODES.contains(code)) {
				result.add(code);
				result.add(code.substring(0, code.indexOf('.')) + "." + Action.VIEW);
			}
		}
		return result;
	}

	public static Set<String> parse(String stored) {
		return stored == null || stored.isBlank() ? Set.of() : normalize(Arrays.asList(stored.split(",")));
	}

	public static String format(Collection<String> codes) {
		return String.join(",", normalize(codes));
	}
}
