package com.laundry.LaundryManagement.auth;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.annotations.Filter;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * Writes the caller's tenant (and, for outlet-level entities, outlet) onto every row saved,
 * whatever the request body said, so a record can never be created in or moved to another
 * tenant or outlet.
 */
public class ScopeStampingListener {

	private record Fields(Field tenant, Field outlet) {
	}

	private static final Map<Class<?>, Fields> FIELDS = new ConcurrentHashMap<>();

	@PrePersist
	@PreUpdate
	public void stamp(Object entity) {
		RequestScope.Scope scope = RequestScope.current();
		if (scope == null || scope.system()) {
			return;
		}
		Fields fields = FIELDS.computeIfAbsent(entity.getClass(), ScopeStampingListener::fieldsOf);
		try {
			if (fields.tenant() != null && scope.tenantId() != null) {
				fields.tenant().set(entity, scope.tenantId());
			}
			if (fields.outlet() != null) {
				if (scope.outletId() == null) {
					throw new IllegalStateException("Choose an outlet before saving " + entity.getClass().getSimpleName());
				}
				fields.outlet().set(entity, String.valueOf(scope.outletId()));
			}
		} catch (IllegalAccessException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private static Fields fieldsOf(Class<?> type) {
		boolean outletScoped = Arrays.stream(type.getAnnotationsByType(Filter.class))
				.anyMatch(filter -> ScopeFilters.OUTLET.equals(filter.name()));
		return new Fields(field(type, "tenantId"), outletScoped ? field(type, "locationID") : null);
	}

	private static Field field(Class<?> type, String name) {
		for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
			try {
				Field field = current.getDeclaredField(name);
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException ignored) {
				// keep looking in the superclass
			}
		}
		return null;
	}
}
