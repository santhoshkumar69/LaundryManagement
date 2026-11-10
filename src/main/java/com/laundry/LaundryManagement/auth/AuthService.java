package com.laundry.LaundryManagement.auth;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.model.OutletDetails;
import com.laundry.LaundryManagement.model.TenantDetails;
import com.laundry.LaundryManagement.model.UserInfo;
import com.laundry.LaundryManagement.model.UserOutlet;
import com.laundry.LaundryManagement.repository.OutletDetailsRepo;
import com.laundry.LaundryManagement.repository.TenantDetailsRepo;
import com.laundry.LaundryManagement.repository.UserInfoRepository;
import com.laundry.LaundryManagement.repository.UserOutletRepo;

/** Login with tenant ID, and choosing the outlet to work in. */
@Service
public class AuthService {

	public record OutletOption(Integer id, String outletCode, String outletName) {
		static OutletOption of(OutletDetails outlet) {
			return new OutletOption(outlet.getId(), outlet.getOutletCode(), outlet.getOutletName());
		}
	}

	public record Session(String token, Long userId, String userName, String role, String tenantCode,
			String companyName, OutletOption outlet, List<OutletOption> outlets) {
	}

	public static class AuthException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		private final int status;

		public AuthException(String message) {
			this(message, 401);
		}

		public AuthException(String message, int status) {
			super(message);
			this.status = status;
		}

		public int getStatus() {
			return status;
		}
	}

	@Autowired
	private TenantDetailsRepo tenantRepo;

	@Autowired
	private UserInfoRepository userRepo;

	@Autowired
	private OutletDetailsRepo outletRepo;

	@Autowired
	private UserOutletRepo userOutletRepo;

	@Autowired
	private AuthTokenService tokens;

	public Session login(String tenantCode, Long loginId, String password) {
		if (tenantCode == null || tenantCode.isBlank() || loginId == null || password == null || password.isEmpty()) {
			throw new AuthException("Enter your tenant ID, login ID and password");
		}
		return RequestScope.asSystem(() -> {
			TenantDetails tenant = tenantRepo.findTenantDetails(tenantCode.trim().toUpperCase());
			if (tenant == null || Objects.equals(tenant.getActiveStatus(), 0)) {
				throw new AuthException("Invalid tenant ID, login ID or password");
			}
			UserInfo user = userRepo.findForLogin(tenant.getId(), loginId, password);
			if (user == null) {
				throw new AuthException("Invalid tenant ID, login ID or password");
			}
			List<OutletOption> outlets = allowedOutlets(user);
			if (outlets.isEmpty()) {
				throw new AuthException("No outlet is assigned to your login. Ask your admin to assign one.");
			}
			OutletOption chosen = outlets.size() == 1 ? outlets.get(0) : null;
			RequestScope.Scope scope = new RequestScope.Scope(user.getId(), user.getUserId(), user.getUserName(),
					user.getRole(), tenant.getId(), chosen == null ? null : chosen.id(), false);
			return new Session(tokens.issue(scope), user.getUserId(), user.getUserName(), user.getRole(),
					tenant.getTenantCode(), tenant.getCompanyName(), chosen, outlets);
		});
	}

	/** Re-issues the caller's token for another outlet they may use. */
	public Session selectOutlet(Integer outletId) {
		RequestScope.Scope scope = RequestScope.require();
		return RequestScope.asSystem(() -> {
			UserInfo user = activeUser(scope);
			List<OutletOption> outlets = allowedOutlets(user);
			OutletOption chosen = outlets.stream().filter(o -> o.id().equals(outletId)).findFirst()
					.orElseThrow(() -> new AuthException("You cannot open that outlet", 403));
			return session(user, scope.tenantId(), chosen, outlets);
		});
	}

	public Session current() {
		RequestScope.Scope scope = RequestScope.require();
		return RequestScope.asSystem(() -> {
			UserInfo user = activeUser(scope);
			List<OutletOption> outlets = allowedOutlets(user);
			OutletOption chosen = outlets.stream().filter(o -> o.id().equals(scope.outletId())).findFirst().orElse(null);
			TenantDetails tenant = tenantRepo.findById(scope.tenantId()).orElseThrow();
			return new Session(null, user.getUserId(), user.getUserName(), user.getRole(), tenant.getTenantCode(),
					tenant.getCompanyName(), chosen, outlets);
		});
	}

	public List<OutletOption> outletsForCurrentUser() {
		RequestScope.Scope scope = RequestScope.require();
		return RequestScope.asSystem(() -> allowedOutlets(activeUser(scope)));
	}

	/** Admins: every outlet of the tenant. Others: assigned outlets, or their home outlet if none are assigned. */
	List<OutletOption> allowedOutlets(UserInfo user) {
		List<OutletDetails> tenantOutlets = outletRepo.findByTenantIdOrderByIdAsc(user.getTenantId());
		if ("admin".equalsIgnoreCase(user.getRole())) {
			return tenantOutlets.stream().map(OutletOption::of).collect(Collectors.toList());
		}
		List<Integer> assigned = userOutletRepo.findByUserDbId(user.getId()).stream().map(UserOutlet::getOutletId)
				.collect(Collectors.toList());
		if (assigned.isEmpty() && user.getLocationID() != null && user.getLocationID().matches("\\d+")) {
			assigned = List.of(Integer.valueOf(user.getLocationID()));
		}
		Map<Integer, OutletDetails> byId = tenantOutlets.stream()
				.collect(Collectors.toMap(OutletDetails::getId, o -> o));
		return assigned.stream().map(byId::get).filter(Objects::nonNull).map(OutletOption::of)
				.collect(Collectors.toList());
	}

	private UserInfo activeUser(RequestScope.Scope scope) {
		UserInfo user = userRepo.findById(scope.userDbId()).orElse(null);
		if (user == null || user.getActiveStatus() != 1 || user.getTenantId() != scope.tenantId()) {
			throw new AuthException("Your login is no longer active");
		}
		return user;
	}

	private Session session(UserInfo user, Integer tenantId, OutletOption chosen, List<OutletOption> outlets) {
		TenantDetails tenant = tenantRepo.findById(tenantId).orElseThrow();
		RequestScope.Scope scope = new RequestScope.Scope(user.getId(), user.getUserId(), user.getUserName(),
				user.getRole(), tenantId, chosen.id(), false);
		return new Session(tokens.issue(scope), user.getUserId(), user.getUserName(), user.getRole(),
				tenant.getTenantCode(), tenant.getCompanyName(), chosen, outlets);
	}
}
