package com.laundry.LaundryManagement.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.model.ItemCategory;
import com.laundry.LaundryManagement.model.ItemMaster;
import com.laundry.LaundryManagement.model.ItemMasterPriceDetails;
import com.laundry.LaundryManagement.model.OutletDetails;
import com.laundry.LaundryManagement.model.TenantDetails;
import com.laundry.LaundryManagement.model.Unit;
import com.laundry.LaundryManagement.model.WashingType;
import com.laundry.LaundryManagement.repository.OutletDetailsRepo;
import com.laundry.LaundryManagement.repository.TenantDetailsRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceContext;

/** Admin-only outlet management for the caller's tenant. */
@Service
public class OutletAdminService {

	public record OutletSummary(Integer id, String outletCode, String outletName, String city, String currency) {
		static OutletSummary of(OutletDetails outlet) {
			return new OutletSummary(outlet.getId(), outlet.getOutletCode(), outlet.getOutletName(), outlet.getCity(),
					outlet.getCurrency());
		}
	}

	public record CreateOutletRequest(String outletName, String city, String state, String country, String postalCode,
			String currency, String mobileNo, String emailId, Integer copyFromOutletId) {
	}

	public record CopySummary(int categories, int units, int laundryTypes, int items, int prices) {
	}

	public record CreateOutletResult(OutletSummary outlet, CopySummary copied) {
	}

	@Autowired
	private OutletDetailsRepo outletRepo;

	@Autowired
	private TenantDetailsRepo tenantRepo;

	@PersistenceContext
	private EntityManager em;

	public List<OutletSummary> list() {
		requireAdmin();
		return outletRepo.findByTenantIdOrderByIdAsc(RequestScope.requireTenantId()).stream().map(OutletSummary::of)
				.toList();
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
		return new CreateOutletResult(OutletSummary.of(outlet), copied);
	}

	/** Copies active categories, units, laundry types, items and prices, remapping the IDs that link them. */
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
			em.flush();
			return new CopySummary(categoryIds.size(), unitIds.size(), washingTypeIds.size(), copiedItems.size(),
					priceCount);
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

	private static void requireAdmin() {
		if (!RequestScope.require().isAdmin()) {
			throw new IllegalArgumentException("Only an admin can manage outlets and users");
		}
	}

	private static String trimToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
