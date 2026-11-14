package com.laundry.LaundryManagement.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.auth.Permissions;
import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.model.RoleDetails;
import com.laundry.LaundryManagement.model.UserInfo;
import com.laundry.LaundryManagement.repository.UserRoleRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/** Admin-only management of the caller's tenant's roles and what each may do. */
@Service
public class RoleAdminService {

	static final String ADMIN_ROLE = "Admin";
	static final String STAFF_ROLE = "Staff";
	private static final int MAX_NAME_LENGTH = 50;

	/** requiresShift: users with the role are cashiers who must start a shift before billing. */
	public record RoleSummary(Integer id, String name, boolean admin, boolean builtIn, Set<String> permissions,
			long users, boolean requiresShift) {
	}

	public record SaveRoleRequest(Integer id, String name, List<String> permissions, Boolean requiresShift) {
	}

	@Autowired
	private UserRoleRepo roleRepo;

	@PersistenceContext
	private EntityManager em;

	public List<Permissions.Screen> catalog() {
		requireAdmin();
		return Permissions.SCREENS;
	}

	public List<RoleSummary> list() {
		requireAdmin();
		Map<String, Long> usersByRole = em.createQuery("from UserInfo u", UserInfo.class).getResultList().stream()
				.filter(u -> u.getRole() != null)
				.collect(Collectors.groupingBy(u -> u.getRole().toLowerCase(), Collectors.counting()));
		return activeRoles().stream()
				.map(role -> summary(role, usersByRole.getOrDefault(role.getUserRole().toLowerCase(), 0L)))
				.toList();
	}

	@Transactional
	public RoleSummary save(SaveRoleRequest request) {
		RequestScope.Scope scope = requireAdmin();
		String name = request == null || request.name() == null ? "" : request.name().trim().replaceAll("\\s+", " ");
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Role name is required");
		}
		if (name.length() > MAX_NAME_LENGTH) {
			throw new IllegalArgumentException("Role name can be at most " + MAX_NAME_LENGTH + " characters");
		}
		if (isAdmin(name)) {
			throw new IllegalArgumentException("Admin is a built-in role with full access; choose another name");
		}
		String permissions = Permissions.format(request.permissions());

		List<RoleDetails> allRoles = em.createQuery("from RoleDetails r", RoleDetails.class).getResultList();
		RoleDetails sameName = allRoles.stream().filter(r -> r.getUserRole().equalsIgnoreCase(name)).findFirst()
				.orElse(null);
		RoleDetails role;
		if (request.id() == null) {
			if (sameName != null && sameName.getActiveStatus() == 1) {
				throw new IllegalArgumentException("A role named " + sameName.getUserRole() + " already exists");
			}
			role = sameName != null ? sameName : new RoleDetails();
			role.setActiveStatus(1);
			role.setCreatedBy(scope.userName());
			role.setCreatedDate(new Date());
		} else {
			role = allRoles.stream().filter(r -> r.getId().equals(request.id()) && r.getActiveStatus() == 1)
					.findFirst().orElseThrow(() -> new IllegalArgumentException("Role not found"));
			if (isAdmin(role.getUserRole())) {
				throw new IllegalArgumentException("The Admin role always has full access and cannot be changed");
			}
			if (sameName != null && !sameName.getId().equals(role.getId())) {
				throw new IllegalArgumentException("A role named " + sameName.getUserRole() + " already exists");
			}
			String oldName = role.getUserRole();
			if (!oldName.equals(name)) {
				em.createQuery("update UserInfo u set u.role = :newName where u.role = :oldName")
						.setParameter("newName", name).setParameter("oldName", oldName).executeUpdate();
			}
			role.setUpdatedBy(scope.userName());
			role.setUpdatedDate(new Date());
		}
		role.setUserRole(name);
		role.setPermissions(permissions);
		role.setRequiresShift(Boolean.TRUE.equals(request.requiresShift()));
		role = roleRepo.save(role);
		long users = em.createQuery("select count(u) from UserInfo u where u.role = :name", Long.class)
				.setParameter("name", name).getSingleResult();
		return summary(role, users);
	}

	/** Hides the role; refused while any user still has it. */
	@Transactional
	public void delete(int roleId) {
		RequestScope.Scope scope = requireAdmin();
		RoleDetails role = activeRoles().stream().filter(r -> r.getId().equals(roleId)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Role not found"));
		if (isAdmin(role.getUserRole())) {
			throw new IllegalArgumentException("The Admin role cannot be deleted");
		}
		List<String> holders = em.createQuery("from UserInfo u where u.role = :name order by u.userName", UserInfo.class)
				.setParameter("name", role.getUserRole()).getResultList().stream()
				.map(u -> u.getUserName() + " (" + u.getUserId() + ")").toList();
		if (!holders.isEmpty()) {
			throw new IllegalArgumentException(role.getUserRole() + " is still given to " + String.join(", ", holders)
					+ ". Change their role in Manage User first.");
		}
		role.setActiveStatus(0);
		role.setUpdatedBy(scope.userName());
		role.setUpdatedDate(new Date());
		roleRepo.save(role);
	}

	private List<RoleDetails> activeRoles() {
		return em.createQuery("from RoleDetails r where r.activeStatus = 1 order by r.id", RoleDetails.class)
				.getResultList();
	}

	private static RoleSummary summary(RoleDetails role, long users) {
		boolean admin = isAdmin(role.getUserRole());
		return new RoleSummary(role.getId(), role.getUserRole(), admin,
				admin || STAFF_ROLE.equalsIgnoreCase(role.getUserRole()),
				admin ? Permissions.all() : Permissions.parse(role.getPermissions()), users,
				!admin && role.isRequiresShift());
	}

	private static boolean isAdmin(String roleName) {
		return ADMIN_ROLE.equalsIgnoreCase(roleName);
	}

	private static RequestScope.Scope requireAdmin() {
		RequestScope.Scope scope = RequestScope.require();
		if (!scope.isAdmin()) {
			throw new IllegalArgumentException("Only an admin can manage roles");
		}
		return scope;
	}
}
