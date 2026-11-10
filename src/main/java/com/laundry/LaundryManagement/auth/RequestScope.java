package com.laundry.LaundryManagement.auth;

import java.util.function.Supplier;

import com.laundry.LaundryManagement.model.GlobalInfo;

/**
 * Who is calling and for which tenant/outlet, for the current request thread. Filled by
 * {@link AuthFilter} from the session token; read by the Hibernate scope filters and by
 * {@link ScopeStampingListener}. With no scope set, scoped queries match nothing.
 */
public final class RequestScope {

	/**
	 * @param userDbId primary key of TB_LA_USER_INFO
	 * @param loginId  the number the user types at login
	 * @param outletId chosen outlet, or null before one is chosen
	 * @param system   internal work (login, sign-up) that must see across tenants
	 */
	public record Scope(Integer userDbId, Long loginId, String userName, String role, Integer tenantId,
			Integer outletId, boolean system) {

		public boolean isAdmin() {
			return "admin".equalsIgnoreCase(role);
		}
	}

	private static final Scope SYSTEM = new Scope(null, null, "system", null, null, null, true);
	private static final ThreadLocal<Scope> CURRENT = new ThreadLocal<>();

	private RequestScope() {
	}

	public static Scope current() {
		return CURRENT.get();
	}

	static void set(Scope scope) {
		CURRENT.set(scope);
	}

	static void clear() {
		CURRENT.remove();
	}

	/** Runs work with cross-tenant visibility, restoring the caller's scope afterwards. */
	public static <T> T asSystem(Supplier<T> work) {
		Scope previous = CURRENT.get();
		CURRENT.set(SYSTEM);
		try {
			return work.get();
		} finally {
			if (previous == null) {
				CURRENT.remove();
			} else {
				CURRENT.set(previous);
			}
		}
	}

	/** Runs work as if the caller had chosen another outlet of their own tenant (e.g. to copy its setup). */
	public static <T> T asOutlet(int outletId, Supplier<T> work) {
		Scope previous = require();
		CURRENT.set(new Scope(previous.userDbId(), previous.loginId(), previous.userName(), previous.role(),
				previous.tenantId(), outletId, false));
		try {
			return work.get();
		} finally {
			CURRENT.set(previous);
		}
	}

	public static Scope require() {
		Scope scope = CURRENT.get();
		if (scope == null || scope.system() || scope.tenantId() == null) {
			throw new IllegalStateException("No signed-in user for this request");
		}
		return scope;
	}

	public static int requireTenantId() {
		return require().tenantId();
	}

	public static int requireOutletId() {
		Scope scope = require();
		if (scope.outletId() == null) {
			throw new IllegalStateException("Choose an outlet first");
		}
		return scope.outletId();
	}

	/** The legacy session object many controllers pass around for created/updated-by stamps. */
	public static GlobalInfo globalInfo() {
		Scope scope = CURRENT.get();
		GlobalInfo.Builder builder = new GlobalInfo.Builder();
		if (scope != null && !scope.system()) {
			builder.setUserId(scope.loginId()).setUserName(scope.userName()).setUserRole(scope.role())
					.setLocationID(scope.outletId() == null ? null : String.valueOf(scope.outletId()));
		}
		return builder.build();
	}
}
