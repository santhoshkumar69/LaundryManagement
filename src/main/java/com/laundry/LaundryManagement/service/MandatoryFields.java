package com.laundry.LaundryManagement.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Billing and customer-creation fields an outlet can make mandatory. Stored on the outlet as a
 * comma-separated list of keys; fields not listed are optional.
 */
public final class MandatoryFields {

	public static final String DELIVERY_DATE = "DELIVERY_DATE";
	public static final String DELIVERY_ADDRESS = "DELIVERY_ADDRESS";
	public static final String PICKUP_TYPE = "PICKUP_TYPE";
	public static final String CUSTOMER_NAME = "CUSTOMER_NAME";
	public static final String CUSTOMER_REMARKS = "CUSTOMER_REMARKS";
	public static final String CUSTOMER_ADDRESS = "CUSTOMER_ADDRESS";

	private static final Map<String, String> LABELS = new LinkedHashMap<>();

	static {
		LABELS.put(DELIVERY_DATE, "Delivery date");
		LABELS.put(DELIVERY_ADDRESS, "Delivery address");
		LABELS.put(PICKUP_TYPE, "Pickup type");
		LABELS.put(CUSTOMER_NAME, "Customer name");
		LABELS.put(CUSTOMER_REMARKS, "Remarks");
		LABELS.put(CUSTOMER_ADDRESS, "Address");
	}

	private MandatoryFields() {
	}

	public static List<String> parse(String stored) {
		if (stored == null || stored.isBlank()) {
			return List.of();
		}
		return Arrays.stream(stored.split(",")).map(String::trim).filter(LABELS::containsKey).distinct().toList();
	}

	/** Comma-separated keys in a fixed order; IllegalArgumentException for unknown keys. */
	public static String format(Collection<String> keys) {
		Set<String> chosen = new LinkedHashSet<>();
		for (String key : keys == null ? List.<String>of() : keys) {
			if (!LABELS.containsKey(key)) {
				throw new IllegalArgumentException("Unknown mandatory field: " + key);
			}
			chosen.add(key);
		}
		return String.join(",", LABELS.keySet().stream().filter(chosen::contains).toList());
	}

	/**
	 * Labels of the required fields whose value is blank, e.g. "Delivery address". The frontend sends
	 * "Invalid date" when the delivery date was cleared, so that counts as blank.
	 */
	public static List<String> missing(Collection<String> required, Map<String, String> values) {
		List<String> missing = new ArrayList<>();
		for (String key : LABELS.keySet()) {
			String value = values.get(key);
			if (required.contains(key) && values.containsKey(key)
					&& (value == null || value.isBlank() || value.trim().equalsIgnoreCase("Invalid date"))) {
				missing.add(LABELS.get(key));
			}
		}
		return missing;
	}

	public static String message(List<String> missing) {
		return "This outlet requires: " + String.join(", ", missing) + ". Fill them in and try again.";
	}
}
