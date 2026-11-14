package com.laundry.LaundryManagement.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.model.Discounts;
import com.laundry.LaundryManagement.model.ExtraCharges;
import com.laundry.LaundryManagement.model.InvoiceTemplate;
import com.laundry.LaundryManagement.model.ItemCategory;
import com.laundry.LaundryManagement.model.ItemMaster;
import com.laundry.LaundryManagement.model.ItemMasterPriceDetails;
import com.laundry.LaundryManagement.model.OutletDetails;
import com.laundry.LaundryManagement.model.PaymentMode;
import com.laundry.LaundryManagement.model.TenantDetails;
import com.laundry.LaundryManagement.model.Unit;
import com.laundry.LaundryManagement.model.UserInfo;
import com.laundry.LaundryManagement.model.UserOutlet;
import com.laundry.LaundryManagement.model.WashingType;
import com.laundry.LaundryManagement.repository.OutletDetailsRepo;
import com.laundry.LaundryManagement.repository.TenantDetailsRepo;
import com.laundry.LaundryManagement.repository.UserOutletRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceContext;

/** Admin-only outlet management for the caller's tenant. */
@Service
public class OutletAdminService {

	public record OutletSummary(Integer id, String outletCode, String outletName, String city, String currency,
			String mobileNo, String emailId, boolean active) {
		static OutletSummary of(OutletDetails outlet) {
			return new OutletSummary(outlet.getId(), outlet.getOutletCode(), outlet.getOutletName(), outlet.getCity(),
					outlet.getCurrency(), outlet.getMobileNo(), outlet.getEmailId(), outlet.getActiveStatus() == 1);
		}
	}

	public record CreateOutletRequest(String outletName, String city, String state, String country, String postalCode,
			String currency, String mobileNo, String emailId, Integer copyFromOutletId) {
	}

	public record CopySummary(int categories, int units, int laundryTypes, int items, int prices, int extraCharges,
			int discounts, int paymentModes) {
	}

	public record CreateOutletResult(OutletSummary outlet, CopySummary copied) {
	}

	@Autowired
	private OutletDetailsRepo outletRepo;

	@Autowired
	private TenantDetailsRepo tenantRepo;

	@Autowired
	private UserOutletRepo userOutletRepo;

	@Autowired
	private PaymentModeDefaults paymentModeDefaults;

	@PersistenceContext
	private EntityManager em;

	public record TemplateChoice(Integer id, String name, Integer outletId, String outletName) {
	}

	/** Every invoice template in the company with its owning outlet; any outlet may print with any of them. */
	public List<TemplateChoice> invoiceTemplates() {
		RequestScope.Scope scope = requireAdmin();
		Map<String, OutletDetails> outlets = new HashMap<>();
		outletRepo.findByTenantIdOrderByIdAsc(scope.tenantId()).forEach(o -> outlets.put(String.valueOf(o.getId()), o));
		List<InvoiceTemplate> templates = RequestScope.asSystem(() -> em
				.createQuery("from InvoiceTemplate t where t.tenantId = :tenantId and t.activeStatus = 1", InvoiceTemplate.class)
				.setParameter("tenantId", scope.tenantId()).getResultList());
		return templates.stream().filter(t -> outlets.containsKey(t.getLocationID()))
				.map(t -> new TemplateChoice(t.getTemplateId(), t.getTemplateName(), outlets.get(t.getLocationID()).getId(),
						outlets.get(t.getLocationID()).getOutletName()))
				.sorted(Comparator.comparing(TemplateChoice::outletId).thenComparing(TemplateChoice::name,
						String.CASE_INSENSITIVE_ORDER))
				.toList();
	}

	public List<OutletSummary> list(boolean includeDeleted) {
		requireAdmin();
		int tenantId = RequestScope.requireTenantId();
		List<OutletDetails> outlets = includeDeleted ? outletRepo.findByTenantIdOrderByIdAsc(tenantId)
				: outletRepo.findActiveByTenantId(tenantId);
		return outlets.stream().map(OutletSummary::of).toList();
	}

	/** Hides the outlet everywhere; its bills, items and history stay in the database. */
	@Transactional
	public OutletSummary delete(int outletId) {
		RequestScope.Scope scope = requireAdmin();
		OutletDetails outlet = outletRepo.findById(outletId)
				.orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
		if (outlet.getActiveStatus() != 1) {
			throw new IllegalArgumentException(outlet.getOutletName() + " is already deleted");
		}
		if (outlet.getId().equals(scope.outletId())) {
			throw new IllegalArgumentException("You are signed in to " + outlet.getOutletName()
					+ ". Switch to another outlet before deleting it.");
		}
		Set<Integer> activeOutletIds = outletRepo.findActiveByTenantId(scope.tenantId()).stream()
				.map(OutletDetails::getId).collect(Collectors.toSet());
		if (activeOutletIds.size() <= 1) {
			throw new IllegalArgumentException("A company needs at least one outlet");
		}
		List<String> stranded = staffWhoseOnlyOutletIs(outletId, activeOutletIds);
		if (!stranded.isEmpty()) {
			throw new IllegalArgumentException(outlet.getOutletName() + " is the only outlet of "
					+ String.join(", ", stranded)
					+ ". Assign them to another outlet in Settings > Manage User, then delete it.");
		}
		outlet.setActiveStatus(0);
		outlet.setUpdatedBy(scope.userName());
		outlet.setUpdatedDate(new Date());
		return OutletSummary.of(outletRepo.save(outlet));
	}

	@Transactional
	public OutletSummary restore(int outletId) {
		RequestScope.Scope scope = requireAdmin();
		OutletDetails outlet = outletRepo.findById(outletId)
				.orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
		if (outlet.getActiveStatus() == 1) {
			throw new IllegalArgumentException(outlet.getOutletName() + " is not deleted");
		}
		outlet.setActiveStatus(1);
		outlet.setUpdatedBy(scope.userName());
		outlet.setUpdatedDate(new Date());
		return OutletSummary.of(outletRepo.save(outlet));
	}

	/** Names of active non-admin users who could open no other active outlet once this one is gone. */
	private List<String> staffWhoseOnlyOutletIs(int outletId, Set<Integer> activeOutletIds) {
		Map<Integer, List<Integer>> assigned = userOutletRepo.findAll().stream().collect(Collectors
				.groupingBy(UserOutlet::getUserDbId, Collectors.mapping(UserOutlet::getOutletId, Collectors.toList())));
		List<String> stranded = new ArrayList<>();
		for (UserInfo user : em.createQuery("from UserInfo u where u.activeStatus = 1 order by u.userName", UserInfo.class)
				.getResultList()) {
			if ("admin".equalsIgnoreCase(user.getRole())) {
				continue;
			}
			List<Integer> outlets = assigned.getOrDefault(user.getId(), List.of());
			if (outlets.isEmpty() && user.getLocationID() != null && user.getLocationID().matches("\\d+")) {
				outlets = List.of(Integer.valueOf(user.getLocationID()));
			}
			Set<Integer> usable = outlets.stream().filter(activeOutletIds::contains).collect(Collectors.toSet());
			if (usable.equals(Set.of(outletId))) {
				stranded.add(user.getUserName() + " (" + user.getUserId() + ")");
			}
		}
		return stranded;
	}

	@Transactional
	public CreateOutletResult create(CreateOutletRequest request) {
		requireAdmin();
		if (request == null || request.outletName() == null || request.outletName().isBlank()) {
			throw new IllegalArgumentException("Outlet name is required");
		}
		int tenantId = RequestScope.requireTenantId();
		TenantDetails tenant = tenantRepo.findById(tenantId).orElseThrow();
		List<OutletDetails> existing = outletRepo.findByTenantIdOrderByIdAsc(tenantId);
		if (existing.stream().anyMatch(o -> o.getOutletName().equalsIgnoreCase(request.outletName().trim()))) {
			throw new IllegalArgumentException("An outlet named " + request.outletName().trim() + " already exists");
		}
		OutletDetails template = outletRepo.findById(RequestScope.requireOutletId()).orElseThrow();
		Integer copyFrom = request.copyFromOutletId();
		if (copyFrom != null && existing.stream().noneMatch(o -> o.getId().equals(copyFrom))) {
			throw new IllegalArgumentException("You can only copy from an outlet of your own company");
		}

		OutletDetails outlet = new OutletDetails();
		outlet.setTenantId(tenantId);
		outlet.setOutletName(request.outletName().trim());
		outlet.setCity(trimToNull(request.city()));
		outlet.setState(trimToNull(request.state()));
		outlet.setCountry(trimToNull(request.country()) != null ? request.country().trim() : template.getCountry());
		outlet.setPostalCode(trimToNull(request.postalCode()));
		outlet.setCurrency(trimToNull(request.currency()) != null ? request.currency().trim().toUpperCase() : template.getCurrency());
		outlet.setMobileNo(trimToNull(request.mobileNo()));
		outlet.setEmailId(trimToNull(request.emailId()));
		outlet.setDateFormat(template.getDateFormat());
		outlet.setTimeFormat(template.getTimeFormat());
		outlet.setTimeZone(template.getTimeZone());
		outlet.setRoundOff(template.getRoundOff());
		outlet.setDecimalSymbol(template.getDecimalSymbol());
		outlet.setFinancialYearStart(template.getFinancialYearStart());
		outlet.setCreatedDate(new Date());
		outlet = outletRepo.save(outlet);
		outlet.setOutletCode(tenant.getTenantCode() + "-" + (existing.size() + 1));
		outlet = outletRepo.save(outlet);
		tenant.setNoOfOutlets(existing.size() + 1);
		tenantRepo.save(tenant);

		CopySummary copied = copyFrom == null ? null : copyItemConfiguration(copyFrom, outlet.getId());
		if (copied == null || copied.paymentModes() == 0) {
			paymentModeDefaults.seed(tenantId, outlet.getId(), RequestScope.require().userName());
		}
		return new CreateOutletResult(OutletSummary.of(outlet), copied);
	}

	/**
	 * Copies active categories, units, laundry types, items, prices, extra charges and discounts, remapping the
	 * IDs that link them.
	 */
	private CopySummary copyItemConfiguration(int fromOutletId, int toOutletId) {
		List<ItemCategory> categories = RequestScope.asOutlet(fromOutletId,
				() -> em.createQuery("from ItemCategory c where c.activeStatus = 1", ItemCategory.class).getResultList());
		List<Unit> units = RequestScope.asOutlet(fromOutletId,
				() -> em.createQuery("from Unit u where u.activeStatus = 1", Unit.class).getResultList());
		List<WashingType> washingTypes = RequestScope.asOutlet(fromOutletId,
				() -> em.createQuery("from WashingType w where w.activeStatus = 1", WashingType.class).getResultList());
		List<ItemMaster> items = RequestScope.asOutlet(fromOutletId,
				() -> em.createQuery("from ItemMaster i where i.activeStatus = 1", ItemMaster.class).getResultList());
		List<ItemMasterPriceDetails> prices = RequestScope.asOutlet(fromOutletId,
				() -> em.createQuery("from ItemMasterPriceDetails p", ItemMasterPriceDetails.class).getResultList());
		List<ExtraCharges> extraCharges = RequestScope.asOutlet(fromOutletId,
				() -> em.createQuery("from ExtraCharges c where c.activeStatus = 1", ExtraCharges.class).getResultList());
		List<Discounts> discounts = RequestScope.asOutlet(fromOutletId,
				() -> em.createQuery("from Discounts d where d.activeStatus = 1", Discounts.class).getResultList());
		List<PaymentMode> paymentModes = RequestScope.asOutlet(fromOutletId,
				() -> em.createQuery("from PaymentMode p where p.activeStatus = 1", PaymentMode.class).getResultList());

		return RequestScope.asOutlet(toOutletId, () -> {
			Map<Integer, Integer> categoryIds = new HashMap<>();
			for (ItemCategory source : categories) {
				ItemCategory copy = copyOf(source);
				em.persist(copy);
				categoryIds.put(source.getId(), copy.getId());
			}
			Map<Integer, Integer> unitIds = new HashMap<>();
			for (Unit source : units) {
				Unit copy = copyOf(source);
				em.persist(copy);
				unitIds.put(source.getId(), copy.getId());
			}
			Map<Integer, Integer> washingTypeIds = new HashMap<>();
			for (WashingType source : washingTypes) {
				WashingType copy = copyOf(source);
				em.persist(copy);
				washingTypeIds.put(source.getProcessTypeId(), copy.getProcessTypeId());
			}
			Map<Integer, Integer> itemIds = new HashMap<>();
			List<ItemMaster> copiedItems = new ArrayList<>();
			for (ItemMaster source : items) {
				Integer categoryId = categoryIds.get(source.getItemCategoryid());
				if (categoryId == null) {
					continue;
				}
				ItemMaster copy = copyOf(source);
				copy.setItemCategoryid(categoryId);
				em.persist(copy);
				itemIds.put(source.getId(), copy.getId());
				copiedItems.add(copy);
			}
			int priceCount = 0;
			for (ItemMasterPriceDetails source : prices) {
				Integer itemId = itemIds.get(source.getItemMasterId());
				Integer unitId = unitIds.get(source.getUnitId());
				Integer washingTypeId = washingTypeIds.get(source.getWashingTypeId());
				if (itemId == null || unitId == null || washingTypeId == null) {
					continue;
				}
				ItemMasterPriceDetails copy = copyOf(source);
				copy.setItemMasterId(itemId);
				copy.setUnitId(unitId);
				copy.setWashingTypeId(washingTypeId);
				em.persist(copy);
				priceCount++;
			}
			for (ExtraCharges source : extraCharges) {
				ExtraCharges copy = copyOf(source);
				copy.setBillingDetails(null);
				em.persist(copy);
			}
			for (Discounts source : discounts) {
				Discounts copy = copyOf(source);
				copy.setBillingDetails(null);
				em.persist(copy);
			}
			for (PaymentMode source : paymentModes) {
				em.persist(copyOf(source));
			}
			em.flush();
			return new CopySummary(categoryIds.size(), unitIds.size(), washingTypeIds.size(), copiedItems.size(),
					priceCount, extraCharges.size(), discounts.size(), paymentModes.size());
		});
	}

	/** A new, unsaved instance with every column except the ID copied (tenant/outlet are stamped on persist). */
	@SuppressWarnings("unchecked")
	private static <T> T copyOf(T source) {
		try {
			var constructor = source.getClass().getDeclaredConstructor();
			constructor.setAccessible(true);
			T copy = (T) constructor.newInstance();
			for (Class<?> type = source.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
				for (Field field : type.getDeclaredFields()) {
					if (Modifier.isStatic(field.getModifiers()) || field.isAnnotationPresent(Id.class)) {
						continue;
					}
					field.setAccessible(true);
					field.set(copy, field.get(source));
				}
			}
			stamp(copy, "createdDate", new Date());
			stamp(copy, "createdBy", RequestScope.require().userName());
			stamp(copy, "updatedDate", null);
			stamp(copy, "updatedBy", null);
			return copy;
		} catch (ReflectiveOperationException ex) {
			throw new IllegalStateException("Could not copy " + source.getClass().getSimpleName(), ex);
		}
	}

	private static void stamp(Object target, String fieldName, Object value) throws IllegalAccessException {
		for (Class<?> type = target.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
			try {
				Field field = type.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(target, value);
				return;
			} catch (NoSuchFieldException ignored) {
				// try the superclass
			}
		}
	}

	private static RequestScope.Scope requireAdmin() {
		RequestScope.Scope scope = RequestScope.require();
		if (!scope.isAdmin()) {
			throw new IllegalArgumentException("Only an admin can manage outlets and users");
		}
		return scope;
	}

	private static String trimToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
