package com.laundry.LaundryManagement.service;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.dto.CompanyProfileDto;
import com.laundry.LaundryManagement.dto.OutletSettingsDto;
import com.laundry.LaundryManagement.dto.RegistrationRequest;
import com.laundry.LaundryManagement.dto.RegistrationResult;
import com.laundry.LaundryManagement.model.OutletDetails;
import com.laundry.LaundryManagement.model.TenantDetails;
import com.laundry.LaundryManagement.auth.LoginIds;
import com.laundry.LaundryManagement.auth.Permissions;
import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.model.RoleDetails;
import com.laundry.LaundryManagement.model.UserInfo;
import com.laundry.LaundryManagement.repository.OutletDetailsRepo;
import com.laundry.LaundryManagement.repository.TenantDetailsRepo;
import com.laundry.LaundryManagement.repository.UserInfoRepository;
import com.laundry.LaundryManagement.repository.UserRoleRepo;

@Service
public class RegistrationServiceImpl implements RegistrationService {

	private static final int MIN_PASSWORD_LENGTH = 6;
	private static final int TENANT_CODE_BASE = 1000;
	private static final String ADMIN_ROLE = "admin";
	private static final List<String> DEFAULT_ROLES = List.of("Admin", "Staff");

	private static final List<String> SUBSCRIPTIONS = List.of("YEARLY", "MONTHLY", "OFFLINE");
	private static final List<String> CUSTOMER_TYPES = List.of("PARTNER", "DIRECT");
	private static final List<String> DATE_FORMATS = List.of("dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "dd-MM-yyyy", "dd.MM.yyyy");
	private static final List<String> TIME_FORMATS = List.of("HH:mm", "hh:mm a");
	private static final List<String> ROUND_OFFS = List.of("NONE", "0.05", "0.10", "0.25", "0.50", "1.00");
	private static final List<String> DECIMAL_SYMBOLS = List.of(".", ",");

	private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
	private static final Pattern PHONE = Pattern.compile("^\\+?[0-9][0-9 \\-]{5,19}$");

	@Autowired
	private TenantDetailsRepo tenantDetailsRepo;

	@Autowired
	private OutletDetailsRepo outletDetailsRepo;

	@Autowired
	private UserInfoRepository userInfoRepository;

	@Autowired
	private UserRoleRepo userRoleRepo;

	// ---- Registration ----

	@Override
	@Transactional
	public RegistrationResult register(RegistrationRequest request) {
		// Nobody is signed in during sign-up, so lookups must see across tenants.
		return RequestScope.asSystem(() -> registerAcrossTenants(request));
	}

	private RegistrationResult registerAcrossTenants(RegistrationRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Registration details are required");
		}
		validateOutletAndContact(request);
		validateLogin(request);

		TenantDetails tenant = request.isExistingCustomer() ? verifiedExistingTenant(request) : createTenant(request);
		if (userInfoRepository.findByTenantAndUserId(tenant.getId(), request.getLoginId()) != null) {
			throw new IllegalArgumentException("Login ID " + request.getLoginId() + " is already taken in this company; choose another");
		}

		long outletCount = outletDetailsRepo.countByTenantId(tenant.getId());
		OutletDetails outlet = new OutletDetails();
		outlet.setTenantId(tenant.getId());
		outlet.setOutletName(request.getOutletName().trim());
		outlet.setCity(trimToNull(request.getCity()));
		outlet.setState(trimToNull(request.getState()));
		outlet.setCountry(trimToNull(request.getCountry()));
		outlet.setPostalCode(trimToNull(request.getPostalCode()));
		outlet.setCurrency(request.getCurrency().trim().toUpperCase());
		outlet.setMobileNo(trimToNull(request.getContactNo()));
		outlet.setEmailId(trimToNull(request.getEmailId()));
		outlet.setDateFormat("dd/MM/yyyy");
		outlet.setTimeFormat("HH:mm");
		outlet.setTimeZone("Asia/Qatar");
		outlet.setRoundOff("NONE");
		outlet.setDecimalSymbol(".");
		outlet.setFinancialYearStart(1);
		outlet.setCreatedDate(new Date());
		outlet = outletDetailsRepo.save(outlet);
		outlet.setOutletCode(tenant.getTenantCode() + "-" + (outletCount + 1));
		outlet = outletDetailsRepo.save(outlet);

		tenant.setNoOfOutlets((int) outletCount + 1);
		tenantDetailsRepo.save(tenant);

		UserInfo admin = new UserInfo();
		admin.setUserId(request.getLoginId());
		admin.setUserName(request.getContactName().trim());
		admin.setPassword(request.getPassword());
		admin.setRole(ADMIN_ROLE);
		admin.setMobileNumber(digitsAsLong(request.getContactNo()));
		admin.setActiveStatus(1);
		admin.setTenantId(tenant.getId());
		admin.setLocationID(String.valueOf(outlet.getId()));
		admin.setCreatedBy("registration");
		admin.setCreatedDate(new Date());
		userInfoRepository.save(admin);

		return new RegistrationResult(tenant.getTenantCode(), tenant.getCompanyName(), outlet.getOutletCode(),
				outlet.getOutletName(), admin.getUserId());
	}

	private TenantDetails createTenant(RegistrationRequest request) {
		require(request.getCompanyName(), "Company name");
		require(request.getSubscription(), "Subscription period");
		String subscription = request.getSubscription().trim().toUpperCase();
		if (!SUBSCRIPTIONS.contains(subscription)) {
			throw new IllegalArgumentException("Subscription period must be yearly, monthly or offline");
		}
		String customerType = trimToNull(request.getCustomerType());
		if (customerType != null && !CUSTOMER_TYPES.contains(customerType.toUpperCase())) {
			throw new IllegalArgumentException("Customer type must be partner or direct customer");
		}
		optionalEmail(request.getAltContact1Email(), "Alternative email 1");
		optionalEmail(request.getAltContact2Email(), "Alternative email 2");
		optionalPhone(request.getAltContact1No(), "Alternative contact number 1");
		optionalPhone(request.getAltContact2No(), "Alternative contact number 2");

		TenantDetails tenant = new TenantDetails();
		tenant.setCompanyName(request.getCompanyName().trim());
		tenant.setContactName(request.getContactName().trim());
		tenant.setContactNo(request.getContactNo().trim());
		tenant.setEmailId(request.getEmailId().trim());
		tenant.setCountry(trimToNull(request.getCountry()));
		tenant.setCustomerType(customerType == null ? null : customerType.toUpperCase());
		tenant.setSubscription(subscription);
		tenant.setAltContact1Name(trimToNull(request.getAltContact1Name()));
		tenant.setAltContact1No(trimToNull(request.getAltContact1No()));
		tenant.setAltContact1Email(trimToNull(request.getAltContact1Email()));
		tenant.setAltContact2Name(trimToNull(request.getAltContact2Name()));
		tenant.setAltContact2No(trimToNull(request.getAltContact2No()));
		tenant.setAltContact2Email(trimToNull(request.getAltContact2Email()));
		tenant.setNoOfOutlets(0);
		tenant.setActiveStatus(1);
		tenant.setCreatedDate(new Date());
		tenant = tenantDetailsRepo.save(tenant);
		tenant.setTenantCode("T" + (TENANT_CODE_BASE + tenant.getId()));
		tenant = tenantDetailsRepo.save(tenant);
		for (String roleName : DEFAULT_ROLES) {
			RoleDetails role = new RoleDetails();
			role.setUserRole(roleName);
			role.setActiveStatus(1);
			role.setPermissions(Permissions.format(Permissions.all()));
			role.setTenantId(tenant.getId());
			role.setCreatedBy("registration");
			role.setCreatedDate(new Date());
			userRoleRepo.save(role);
		}
		return tenant;
	}

	private TenantDetails verifiedExistingTenant(RegistrationRequest request) {
		require(request.getTenantCode(), "Tenant ID");
		TenantDetails tenant = tenantDetailsRepo.findTenantDetails(request.getTenantCode().trim().toUpperCase());
		if (tenant == null) {
			throw new IllegalArgumentException("No company found with tenant ID " + request.getTenantCode().trim());
		}
		if (isBlank(request.getVerifyUserId()) || isBlank(request.getVerifyPassword())) {
			throw new IllegalArgumentException("Enter the login ID and password of an existing admin of this company");
		}
		UserInfo verifier = userInfoRepository.findForLogin(tenant.getId(), request.getVerifyUserId().trim(), request.getVerifyPassword());
		if (verifier == null || !ADMIN_ROLE.equalsIgnoreCase(verifier.getRole())) {
			throw new IllegalArgumentException("That admin login does not belong to tenant " + tenant.getTenantCode());
		}
		return tenant;
	}

	private void validateOutletAndContact(RegistrationRequest request) {
		require(request.getOutletName(), "Outlet name");
		require(request.getCity(), "City");
		require(request.getCountry(), "Country");
		require(request.getCurrency(), "Currency");
		validCurrency(request.getCurrency());
		require(request.getContactName(), "Contact name");
		require(request.getContactNo(), "Contact number");
		optionalPhone(request.getContactNo(), "Contact number");
		require(request.getEmailId(), "Email ID");
		optionalEmail(request.getEmailId(), "Email ID");
	}

	private void validateLogin(RegistrationRequest request) {
		request.setLoginId(LoginIds.requireValid(request.getLoginId()));
		if (request.getPassword() == null || request.getPassword().length() < MIN_PASSWORD_LENGTH) {
			throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
		}
	}

	// ---- Company profile ----

	@Override
	public CompanyProfileDto getCompanyProfile(String userId) {
		TenantDetails tenant = tenantOf(activeUser(userId));
		CompanyProfileDto profile = new CompanyProfileDto();
		profile.setUserId(userId);
		profile.setTenantCode(tenant.getTenantCode());
		profile.setCompanyName(tenant.getCompanyName());
		profile.setEmailId(tenant.getEmailId());
		profile.setContactName(tenant.getContactName());
		profile.setContactNo(tenant.getContactNo());
		profile.setCountry(tenant.getCountry());
		return profile;
	}

	@Override
	@Transactional
	public CompanyProfileDto updateCompanyProfile(CompanyProfileDto profile) {
		if (profile == null) {
			throw new IllegalArgumentException("Company profile is required");
		}
		UserInfo user = adminUser(profile.getUserId());
		TenantDetails tenant = tenantOf(user);
		require(profile.getCompanyName(), "Company name");
		require(profile.getContactName(), "Contact name");
		require(profile.getContactNo(), "Contact number");
		optionalPhone(profile.getContactNo(), "Contact number");
		require(profile.getEmailId(), "Email ID");
		optionalEmail(profile.getEmailId(), "Email ID");
		require(profile.getCountry(), "Country");

		tenant.setCompanyName(profile.getCompanyName().trim());
		tenant.setContactName(profile.getContactName().trim());
		tenant.setContactNo(profile.getContactNo().trim());
		tenant.setEmailId(profile.getEmailId().trim());
		tenant.setCountry(profile.getCountry().trim());
		tenant.setUpdatedBy(user.getUserName());
		tenant.setUpdatedDate(new Date());
		tenantDetailsRepo.save(tenant);
		return getCompanyProfile(profile.getUserId());
	}

	// ---- Outlet settings ----

	@Override
	public OutletSettingsDto getOutletSettings(String userId, Integer outletId) {
		UserInfo user = activeUser(userId);
		if (outletId != null && !outletId.equals(RequestScope.requireOutletId())) {
			adminUser(userId);
		}
		OutletDetails outlet = outletOf(outletId);
		OutletSettingsDto settings = new OutletSettingsDto();
		settings.setUserId(user.getUserId());
		settings.setOutletId(outlet.getId());
		settings.setOutletCode(outlet.getOutletCode());
		settings.setOutletName(outlet.getOutletName());
		settings.setDateFormat(outlet.getDateFormat());
		settings.setTimeFormat(outlet.getTimeFormat());
		settings.setTimeZone(outlet.getTimeZone());
		settings.setRoundOff(outlet.getRoundOff());
		settings.setDecimalSymbol(outlet.getDecimalSymbol());
		settings.setFinancialYearStart(outlet.getFinancialYearStart());
		settings.setPostalCode(outlet.getPostalCode());
		settings.setCity(outlet.getCity());
		settings.setState(outlet.getState());
		settings.setCountry(outlet.getCountry());
		settings.setCurrency(outlet.getCurrency());
		settings.setMobileNo(outlet.getMobileNo());
		settings.setEmailId(outlet.getEmailId());
		settings.setAddress(outlet.getAddress());
		settings.setRegNo1(outlet.getRegNo1());
		settings.setRegNo2(outlet.getRegNo2());
		settings.setLogoData(outlet.getLogoData());
		return settings;
	}

	@Override
	@Transactional
	public OutletSettingsDto updateOutletSettings(OutletSettingsDto settings) {
		if (settings == null) {
			throw new IllegalArgumentException("Outlet settings are required");
		}
		UserInfo user = adminUser(settings.getUserId());
		OutletDetails outlet = outletOf(settings.getOutletId());
		require(settings.getOutletName(), "Outlet name");
		String outletName = settings.getOutletName().trim();
		if (outletDetailsRepo.findByTenantIdOrderByIdAsc(outlet.getTenantId()).stream()
				.anyMatch(o -> !o.getId().equals(outlet.getId()) && outletName.equalsIgnoreCase(o.getOutletName()))) {
			throw new IllegalArgumentException("Another outlet is already named " + outletName);
		}
		oneOf(settings.getDateFormat(), DATE_FORMATS, "Date format");
		oneOf(settings.getTimeFormat(), TIME_FORMATS, "Time format");
		oneOf(settings.getRoundOff(), ROUND_OFFS, "Round off");
		oneOf(settings.getDecimalSymbol(), DECIMAL_SYMBOLS, "Decimal symbol");
		require(settings.getTimeZone(), "Time zone");
		try {
			ZoneId.of(settings.getTimeZone().trim());
		} catch (DateTimeException ex) {
			throw new IllegalArgumentException("Unknown time zone: " + settings.getTimeZone());
		}
		Integer fyStart = settings.getFinancialYearStart();
		if (fyStart == null || fyStart < 1 || fyStart > 12) {
			throw new IllegalArgumentException("Financial year must start in a month from January to December");
		}
		require(settings.getCurrency(), "Currency");
		validCurrency(settings.getCurrency());
		optionalPhone(settings.getMobileNo(), "Mobile number");
		optionalEmail(settings.getEmailId(), "Email ID");
		if (settings.getLogoData() != null && !settings.getLogoData().isEmpty()
				&& !settings.getLogoData().startsWith("data:image/")) {
			throw new IllegalArgumentException("Logo must be an image");
		}

		outlet.setOutletName(settings.getOutletName().trim());
		outlet.setDateFormat(settings.getDateFormat());
		outlet.setTimeFormat(settings.getTimeFormat());
		outlet.setTimeZone(settings.getTimeZone().trim());
		outlet.setRoundOff(settings.getRoundOff());
		outlet.setDecimalSymbol(settings.getDecimalSymbol());
		outlet.setFinancialYearStart(fyStart);
		outlet.setPostalCode(trimToNull(settings.getPostalCode()));
		outlet.setCity(trimToNull(settings.getCity()));
		outlet.setState(trimToNull(settings.getState()));
		outlet.setCountry(trimToNull(settings.getCountry()));
		outlet.setCurrency(settings.getCurrency().trim().toUpperCase());
		outlet.setMobileNo(trimToNull(settings.getMobileNo()));
		outlet.setEmailId(trimToNull(settings.getEmailId()));
		outlet.setAddress(trimToNull(settings.getAddress()));
		outlet.setRegNo1(trimToNull(settings.getRegNo1()));
		outlet.setRegNo2(trimToNull(settings.getRegNo2()));
		outlet.setLogoData(isBlank(settings.getLogoData()) ? null : settings.getLogoData());
		outlet.setUpdatedBy(user.getUserName());
		outlet.setUpdatedDate(new Date());
		outletDetailsRepo.save(outlet);
		return getOutletSettings(settings.getUserId(), outlet.getId());
	}

	// ---- Lookups ----

	/** The signed-in user; the userId sent by older clients is ignored in favour of the session. */
	private UserInfo activeUser(String ignoredUserId) {
		UserInfo user = userInfoRepository.findById(RequestScope.require().userDbId()).orElse(null);
		if (user == null || user.getActiveStatus() != 1) {
			throw new IllegalArgumentException("Your login is no longer active");
		}
		return user;
	}

	private UserInfo adminUser(String ignoredUserId) {
		UserInfo user = activeUser(ignoredUserId);
		if (!ADMIN_ROLE.equalsIgnoreCase(user.getRole())) {
			throw new IllegalArgumentException("Only an admin can change these settings");
		}
		return user;
	}

	private TenantDetails tenantOf(UserInfo user) {
		return tenantDetailsRepo.findById(RequestScope.requireTenantId())
				.orElseThrow(() -> new IllegalArgumentException("Your login is not linked to a registered company"));
	}

	/** The given active outlet of the caller's company, or the outlet chosen for this session. */
	private OutletDetails outletOf(Integer outletId) {
		OutletDetails outlet = outletDetailsRepo.findById(outletId != null ? outletId : RequestScope.requireOutletId())
				.orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
		if (outlet.getActiveStatus() != 1) {
			throw new IllegalArgumentException(outlet.getOutletName() + " is deleted; restore it before editing");
		}
		return outlet;
	}

	// ---- Validation helpers ----

	private static void require(String value, String label) {
		if (isBlank(value)) {
			throw new IllegalArgumentException(label + " is required");
		}
	}

	private static void oneOf(String value, List<String> allowed, String label) {
		if (value == null || !allowed.contains(value)) {
			throw new IllegalArgumentException(label + " must be one of " + String.join(", ", allowed));
		}
	}

	private static void optionalEmail(String value, String label) {
		if (!isBlank(value) && !EMAIL.matcher(value.trim()).matches()) {
			throw new IllegalArgumentException(label + " is not a valid email address");
		}
	}

	private static void optionalPhone(String value, String label) {
		if (!isBlank(value) && !PHONE.matcher(value.trim()).matches()) {
			throw new IllegalArgumentException(label + " must contain only digits, spaces, dashes and an optional leading +");
		}
	}

	private static void validCurrency(String code) {
		try {
			Currency.getInstance(code.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Unknown currency code: " + code);
		}
	}

	private static Long digitsAsLong(String phone) {
		String digits = phone == null ? "" : phone.replaceAll("\\D", "");
		if (digits.isEmpty() || digits.length() > 18) {
			return null;
		}
		return Long.valueOf(digits);
	}

	private static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private static String trimToNull(String value) {
		return isBlank(value) ? null : value.trim();
	}
}
