package com.laundry.LaundryManagement.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Notes and coins counted when a shift is closed, by the outlet's currency (largest first). */
public final class ShiftDenominations {

	public static final String NOTE = "NOTE";
	public static final String COIN = "COIN";

	public record Denomination(BigDecimal value, String kind) {
	}

	private record Series(String notes, String coins) {
	}

	private static final Series DEFAULT = new Series("1000 500 200 100 50 20 10 5", "2 1 0.50 0.25 0.10 0.05 0.01");

	private static final Map<String, Series> BY_CURRENCY = Map.of(
			"QAR", new Series("500 200 100 50 10 5 1", "0.50 0.25 0.10 0.05 0.01"),
			"INR", new Series("500 200 100 50 20 10", "5 2 1"),
			"AED", new Series("1000 500 200 100 50 20 10 5", "1 0.50 0.25"),
			"SAR", new Series("500 200 100 50 10 5", "2 1 0.50 0.25 0.10 0.05"),
			"USD", new Series("100 50 20 10 5 2 1", "0.25 0.10 0.05 0.01"),
			"EUR", new Series("500 200 100 50 20 10 5", "2 1 0.50 0.20 0.10 0.05 0.02 0.01"),
			"GBP", new Series("50 20 10 5", "2 1 0.50 0.20 0.10 0.05 0.02 0.01"));

	private ShiftDenominations() {
	}

	static List<Denomination> forCurrency(String currency) {
		Series set = BY_CURRENCY.getOrDefault(currency == null ? "" : currency.trim().toUpperCase(Locale.ROOT), DEFAULT);
		List<Denomination> list = new ArrayList<>();
		add(list, set.notes(), NOTE);
		add(list, set.coins(), COIN);
		return list;
	}

	private static void add(List<Denomination> list, String values, String kind) {
		for (String value : values.split(" ")) {
			list.add(new Denomination(new BigDecimal(value).setScale(2), kind));
		}
	}
}
