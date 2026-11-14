package com.laundry.LaundryManagement.auth;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.laundry.LaundryManagement.repository.OutletDetailsRepo;

/**
 * IDs of outlets an admin has deleted, so sessions still pointing at one can be refused without a
 * database lookup per request. Call {@link #refresh()} after an outlet is deleted or restored.
 */
@Component
public class DeletedOutlets {

	private final OutletDetailsRepo outletRepo;
	private volatile Set<Integer> ids;

	public DeletedOutlets(OutletDetailsRepo outletRepo) {
		this.outletRepo = outletRepo;
	}

	public boolean contains(Integer outletId) {
		return outletId != null && ids().contains(outletId);
	}

	public void refresh() {
		ids = null;
	}

	private Set<Integer> ids() {
		Set<Integer> current = ids;
		if (current == null) {
			current = RequestScope.asSystem(() -> Set.copyOf(outletRepo.findDeletedIds()));
			ids = current;
		}
		return current;
	}
}
