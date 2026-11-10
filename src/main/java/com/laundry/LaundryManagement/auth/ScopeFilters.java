package com.laundry.LaundryManagement.auth;

import java.util.function.Supplier;

/**
 * Names and parameter resolvers of the Hibernate filters that keep each tenant's (and each
 * outlet's) rows apart. The filters are declared on TenantDetails and attached to every scoped
 * entity with @Filter; they are auto-enabled and also apply to find-by-id.
 */
public final class ScopeFilters {

	public static final String TENANT = "tenantFilter";
	public static final String OUTLET = "outletFilter";

	public static final String TENANT_CONDITION = "(:tenantId = " + ScopeFilters.ANY_TENANT + " or TENANT_ID = :tenantId)";
	public static final String OUTLET_CONDITION = "(:outletId = '" + ScopeFilters.ANY_OUTLET + "' or LOCATION_ID = :outletId)";

	static final int ANY_TENANT = -1;
	static final int NO_TENANT = -2;
	static final String ANY_OUTLET = "*";
	static final String NO_OUTLET = "#none";

	private ScopeFilters() {
	}

	public static class TenantIdResolver implements Supplier<Integer> {
		@Override
		public Integer get() {
			RequestScope.Scope scope = RequestScope.current();
			if (scope == null) {
				return NO_TENANT;
			}
			if (scope.system()) {
				return ANY_TENANT;
			}
			return scope.tenantId() == null ? NO_TENANT : scope.tenantId();
		}
	}

	public static class OutletIdResolver implements Supplier<String> {
		@Override
		public String get() {
			RequestScope.Scope scope = RequestScope.current();
			if (scope == null) {
				return NO_OUTLET;
			}
			if (scope.system()) {
				return ANY_OUTLET;
			}
			return scope.outletId() == null ? NO_OUTLET : String.valueOf(scope.outletId());
		}
	}
}
