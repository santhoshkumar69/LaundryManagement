package com.laundry.LaundryManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.model.OutletSequence;
import com.laundry.LaundryManagement.repository.OutletSequenceRepo;

/** Gap-free running numbers per outlet, e.g. BILL1, BILL2, … for each outlet separately. */
@Service
public class OutletSequenceService {

	public static final String RECEIPT = "BILL";
	public static final String PAYMENT = "PAY";

	@Autowired
	private OutletSequenceRepo repo;

	/** Next number of the series for the caller's outlet, formatted with the series prefix. */
	@Transactional(propagation = Propagation.REQUIRED)
	public String next(String series) {
		int outletId = RequestScope.requireOutletId();
		OutletSequence sequence = repo.findForUpdate(outletId, series).orElseGet(() -> {
			OutletSequence created = new OutletSequence();
			created.setOutletId(outletId);
			created.setSeries(series);
			created.setLastValue(0);
			return created;
		});
		sequence.setLastValue(sequence.getLastValue() + 1);
		repo.save(sequence);
		return series + sequence.getLastValue();
	}
}
