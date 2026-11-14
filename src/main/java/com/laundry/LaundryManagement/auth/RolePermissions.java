package com.laundry.LaundryManagement.auth;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.laundry.LaundryManagement.model.RoleDetails;
import com.laundry.LaundryManagement.repository.UserRoleRepo;

/**
 * What the signed-in user's role may do. Cached per tenant and role name; call {@link #refresh()}
 * after a role is saved or deleted.
 */
@Component
public class RolePermissions {

	@ResponseStatus(HttpStatus.FORBIDDEN)
	public static class PermissionDeniedException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public PermissionDeniedException() {
			super("Your role does not allow this. Ask your admin for access.");
		}
	}

	private final UserRoleRepo roleRepo;
	private final Map<String, Set<String>> cache = new ConcurrentHashMap<>();

	public RolePermissions(UserRoleRepo roleRepo) {
		this.roleRepo = roleRepo;
	}

	public Set<String> of(RequestScope.Scope scope) {
		return of(scope.tenantId(), scope.role());
	}

	public Set<String> of(Integer tenantId, String role) {
		if ("admin".equalsIgnoreCase(role)) {
			return Permissions.all();
		}
		if (tenantId == null || role == null) {
			return Set.of();
		}
		return cache.computeIfAbsent(tenantId + "|" + role.toLowerCase(), key -> RequestScope.asSystem(() -> {
			RoleDetails details = roleRepo.findActiveRole(tenantId, role);
			return details == null ? Set.of() : Permissions.parse(details.getPermissions());
		}));
	}

	public boolean allowsAny(RequestScope.Scope scope, Collection<String> codes) {
		Set<String> granted = of(scope);
		return codes.stream().anyMatch(granted::contains);
	}

	/** Throws a 403 unless the current user's role has the permission. */
	public void require(String screen, Permissions.Action action) {
		if (!of(RequestScope.require()).contains(Permissions.code(screen, action))) {
			throw new PermissionDeniedException();
		}
	}

	public void refresh() {
		cache.clear();
	}
}
