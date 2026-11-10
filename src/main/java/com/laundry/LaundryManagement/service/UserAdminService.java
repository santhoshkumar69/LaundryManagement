package com.laundry.LaundryManagement.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.auth.LoginIds;
import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.model.OutletDetails;
import com.laundry.LaundryManagement.model.RoleDetails;
import com.laundry.LaundryManagement.model.UserInfo;
import com.laundry.LaundryManagement.model.UserOutlet;
import com.laundry.LaundryManagement.repository.OutletDetailsRepo;
import com.laundry.LaundryManagement.repository.UserInfoRepository;
import com.laundry.LaundryManagement.repository.UserOutletRepo;
import com.laundry.LaundryManagement.repository.UserRoleRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/** Admin-only management of the caller's tenant's users and the outlets they may open. */
@Service
public class UserAdminService {

	private static final int MIN_PASSWORD_LENGTH = 6;

	public record UserSummary(Integer id, String loginId, String userName, String role, Long mobileNumber, boolean active,
			List<Integer> outletIds) {
	}

	public record SaveUserRequest(Integer id, String loginId, String userName, String password, String role,
			Long mobileNumber, List<Integer> outletIds) {
	}

	@Autowired
	private UserInfoRepository userRepo;

	@Autowired
	private UserOutletRepo userOutletRepo;

	@Autowired
	private OutletDetailsRepo outletRepo;

	@Autowired
	private UserRoleRepo roleRepo;

	@PersistenceContext
	private EntityManager em;

	public List<UserSummary> list() {
		requireAdmin();
		Map<Integer, List<Integer>> outletsByUser = userOutletRepo.findAll().stream()
				.collect(Collectors.groupingBy(UserOutlet::getUserDbId,
						Collectors.mapping(UserOutlet::getOutletId, Collectors.toList())));
		return em.createQuery("from UserInfo u order by u.userName", UserInfo.class).getResultList().stream()
				.map(u -> new UserSummary(u.getId(), u.getUserId(), u.getUserName(), u.getRole(), u.getMobileNumber(),
						u.getActiveStatus() == 1, outletsByUser.getOrDefault(u.getId(), List.of())))
				.toList();
	}

	public List<String> roles() {
		return em.createQuery("from RoleDetails r where r.activeStatus = 1 order by r.userRole", RoleDetails.class)
				.getResultList().stream().map(RoleDetails::getUserRole).toList();
	}

	@Transactional
	public UserSummary save(SaveUserRequest request) {
		RequestScope.Scope scope = requireAdmin();
		if (request == null) {
			throw new IllegalArgumentException("User details are required");
		}
		if (request.userName() == null || request.userName().isBlank()) {
			throw new IllegalArgumentException("Name is required");
		}
		String role = roles().stream().filter(r -> r.equalsIgnoreCase(request.role() == null ? "" : request.role().trim()))
				.findFirst().orElseThrow(() -> new IllegalArgumentException("Choose one of the roles: " + String.join(", ", roles())));
		boolean admin = "admin".equalsIgnoreCase(role);

		Set<Integer> tenantOutletIds = outletRepo.findByTenantIdOrderByIdAsc(scope.tenantId()).stream()
				.map(OutletDetails::getId).collect(Collectors.toSet());
		List<Integer> outletIds = request.outletIds() == null ? List.of()
				: request.outletIds().stream().distinct().toList();
		if (!tenantOutletIds.containsAll(outletIds)) {
			throw new IllegalArgumentException("You can only assign outlets of your own company");
		}
		if (!admin && outletIds.isEmpty()) {
			throw new IllegalArgumentException("Assign at least one outlet to a non-admin user");
		}

		UserInfo user;
		if (request.id() == null) {
			String loginId = LoginIds.requireValid(request.loginId());
			if (userRepo.findByTenantAndUserId(scope.tenantId(), loginId) != null) {
				throw new IllegalArgumentException("Login ID " + loginId + " is already used in your company");
			}
			requirePassword(request.password());
			user = new UserInfo();
			user.setUserId(loginId);
			user.setPassword(request.password());
			user.setActiveStatus(1);
			user.setCreatedBy(scope.userName());
			user.setCreatedDate(new Date());
		} else {
			user = userRepo.findById(request.id()).orElseThrow(() -> new IllegalArgumentException("User not found"));
			if (user.getId().equals(scope.userDbId()) && !admin) {
				throw new IllegalArgumentException("You cannot remove your own admin role");
			}
			if (request.password() != null && !request.password().isEmpty()) {
				requirePassword(request.password());
				user.setPassword(request.password());
			}
			user.setUpdatedBy(scope.userName());
			user.setUpdatedDate(new Date());
		}
		user.setUserName(request.userName().trim());
		user.setRole(role);
		user.setMobileNumber(request.mobileNumber());
		user.setTenantId(scope.tenantId());
		user.setLocationID(outletIds.isEmpty() ? String.valueOf(scope.outletId()) : String.valueOf(outletIds.get(0)));
		user = userRepo.save(user);

		userOutletRepo.deleteByUserDbId(user.getId());
		for (Integer outletId : admin ? List.<Integer>of() : outletIds) {
			UserOutlet link = new UserOutlet();
			link.setUserDbId(user.getId());
			link.setOutletId(outletId);
			link.setTenantId(scope.tenantId());
			userOutletRepo.save(link);
		}
		return new UserSummary(user.getId(), user.getUserId(), user.getUserName(), user.getRole(),
				user.getMobileNumber(), user.getActiveStatus() == 1, admin ? List.of() : outletIds);
	}

	@Transactional
	public void setActive(Integer userDbId, boolean active) {
		RequestScope.Scope scope = requireAdmin();
		UserInfo user = userRepo.findById(userDbId).orElseThrow(() -> new IllegalArgumentException("User not found"));
		if (user.getId().equals(scope.userDbId()) && !active) {
			throw new IllegalArgumentException("You cannot deactivate your own login");
		}
		user.setActiveStatus(active ? 1 : 0);
		user.setUpdatedBy(scope.userName());
		user.setUpdatedDate(new Date());
		userRepo.save(user);
	}

	private static void requirePassword(String password) {
		if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
			throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
		}
	}

	private static RequestScope.Scope requireAdmin() {
		RequestScope.Scope scope = RequestScope.require();
		if (!scope.isAdmin()) {
			throw new IllegalArgumentException("Only an admin can manage outlets and users");
		}
		return scope;
	}
}
