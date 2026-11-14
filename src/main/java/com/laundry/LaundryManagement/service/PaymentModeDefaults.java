package com.laundry.LaundryManagement.service;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.model.PaymentMode;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/** Gives a new outlet the standard Cash (default) and Card payment modes. */
@Component
public class PaymentModeDefaults {

	@PersistenceContext
	private EntityManager em;

	public void seed(int tenantId, int outletId, String createdBy) {
		RequestScope.Scope scope = RequestScope.current();
		if (scope == null || scope.system()) {
			persistDefaults(tenantId, outletId, createdBy);
		} else {
			// Saves are stamped with the scope's outlet, so work as the new outlet.
			RequestScope.asOutlet(outletId, () -> {
				persistDefaults(tenantId, outletId, createdBy);
				return null;
			});
		}
	}

	private void persistDefaults(int tenantId, int outletId, String createdBy) {
		PaymentMode cash = mode("Cash", false, tenantId, outletId, createdBy);
		cash.setDefaultMode(true);
		em.persist(cash);
		em.persist(mode("Card", true, tenantId, outletId, createdBy));
		em.flush();
	}

	private static PaymentMode mode(String name, boolean needsCardType, int tenantId, int outletId, String createdBy) {
		PaymentMode mode = new PaymentMode();
		mode.setPaymentMode(name);
		mode.setNeedsCardType(needsCardType);
		mode.setActiveStatus(1);
		mode.setTenantId(tenantId);
		mode.setLocationID(String.valueOf(outletId));
		mode.setCreatedBy(createdBy);
		mode.setCreatedDate(new Date());
		return mode;
	}
}
