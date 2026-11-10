package com.laundry.LaundryManagement.auth;

import java.util.regex.Pattern;

/**
 * Format of the ID a user types at login. Matching is case-insensitive because the user table's
 * collation is; the unique (tenant, login ID) index relies on the same collation.
 */
public final class LoginIds {

	private static final Pattern FORMAT = Pattern.compile("[A-Za-z0-9._@-]{3,50}");

	private LoginIds() {
	}

	/** Trimmed login ID for a new user, or IllegalArgumentException if it doesn't fit the format. */
	public static String requireValid(String loginId) {
		String id = loginId == null ? "" : loginId.trim();
		if (!FORMAT.matcher(id).matches()) {
			throw new IllegalArgumentException(
					"Login ID must be 3 to 50 characters: letters, numbers, dot (.), underscore (_), hyphen (-) or @");
		}
		return id;
	}
}
